/******************************************************************************
 * Copyright (c) 2018 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
package com.ibm.wala.cast.python.analysis;

import java.util.Map;
import java.util.Set;

import com.ibm.wala.cast.lsp.AnalysisError;
import com.ibm.wala.cast.python.types.TensorType;
import com.ibm.wala.cast.python.types.TensorType.Dimension;
import com.ibm.wala.dataflow.graph.AbstractMeetOperator;
import com.ibm.wala.dataflow.graph.DataflowSolver;
import com.ibm.wala.dataflow.graph.IKilldallFramework;
import com.ibm.wala.dataflow.graph.ITransferFunctionProvider;
import com.ibm.wala.fixpoint.UnaryOperator;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PointsToSetVariable;
import com.ibm.wala.util.graph.Graph;

public class TensorTypeAnalysis extends DataflowSolver<PointsToSetVariable, TensorVariable> {

	static class ReshapeError implements AnalysisError {
		ReshapeError(TensorType from, TensorType to) {
			this.from = from;
			this.to = to;
		}
		TensorType from, to;

		public String toString() {
			return toString(false);
		}

		public String toString(boolean useMarkdown) {
			return "Cannot reshape " + from.toCString(useMarkdown) + " to " + to.toCString(useMarkdown);
		}

	}

	static class ConvError implements AnalysisError {
		ConvError(TensorType from, int dims) {
			this.from = from;
			this.dims = dims;
		}
		TensorType from;
		int dims;

		public String toString() {
			return toString(false);
		}

		public String toString(boolean useMarkdown) {
			return "bad type to convolve " + from.toCString(useMarkdown) + ", needs " + (dims+2) + " dimensions";
		}

	}

	private static IKilldallFramework<PointsToSetVariable, TensorVariable> createProblem(Graph<PointsToSetVariable> G, Map<PointsToSetVariable,TensorType> reshapeNodes, Map<PointsToSetVariable, TensorType> set_shapes, Set<PointsToSetVariable> conv2ds, Set<PointsToSetVariable> conv3ds, Map<PointerKey, AnalysisError> errorLog) {
		return new IKilldallFramework<PointsToSetVariable, TensorVariable>() {

			@Override
			public Graph<PointsToSetVariable> getFlowGraph() {
				return G;
			}

			@Override
			public ITransferFunctionProvider<PointsToSetVariable, TensorVariable> getTransferFunctionProvider() {
				return new ITransferFunctionProvider<PointsToSetVariable, TensorVariable>() {
					
					final class SetShapeOp extends UnaryOperator<TensorVariable> {
						private final TensorType setShapeTo;
						
						public SetShapeOp(TensorType reshapeTo) {
							this.setShapeTo = reshapeTo;
						}

						@Override
						public byte evaluate(TensorVariable lhs, TensorVariable rhs) {
							return lhs.state.add(setShapeTo)? CHANGED_AND_FIXED: NOT_CHANGED;
						}
						
						@Override
						public int hashCode() {
							return setShapeTo.hashCode();
						}

						@Override
						public boolean equals(Object o) {
							return this == o || ((o instanceof ReshapeOp) && setShapeTo.equals(((ReshapeOp)o).reshapeTo));
						}

						@Override
						public String toString() {
							return "set shape to " + setShapeTo;
						}
					}
					
					final class ConvOp extends UnaryOperator<TensorVariable> {
						private final PointsToSetVariable v;
						private final int dimensions;
						
						public ConvOp(int dimensions, PointsToSetVariable v) {
							this.v = v;
							this.dimensions = dimensions;
						}

						@Override
						public byte evaluate(TensorVariable lhs, TensorVariable rhs) {
							boolean changed = false;
							if (rhs != null && rhs.state != null) {
								for(TensorType t : rhs.state) {
									int dims = 0;
									for(@SuppressWarnings("unused") Dimension<?> d : t) {
										dims++;
									}
									if (dims == dimensions+2) {
										changed |= lhs.state.add(t);
									} else {
										errorLog.put(v.getPointerKey(), new ConvError(t, dimensions));
									}
								}
							}
							return changed? CHANGED_AND_FIXED: NOT_CHANGED;
						}

						@Override
						public int hashCode() {
							return v.hashCode();
						}

						@Override
						public boolean equals(Object o) {
							return (o instanceof ConvOp) && ((ConvOp)o).v.equals(v);
						}

						@Override
						public String toString() {
							return "conv at " + v;
						}
					}
					
					final class ReshapeOp extends UnaryOperator<TensorVariable> {
						private final TensorType reshapeTo;
						private final PointsToSetVariable v;
						
						public ReshapeOp(TensorType reshapeTo, PointsToSetVariable v) {
							this.v = v;
							this.reshapeTo = reshapeTo;
						}

						@Override
						public byte evaluate(TensorVariable lhs, TensorVariable rhs) {
							boolean changed = false;
							int ssz = reshapeTo.symbolicDims();
							int csz = reshapeTo.concreteSize();
							if (rhs != null && rhs.state != null) {
								for(TensorType t : rhs.state) {
									if (t.symbolicDims() == ssz && t.concreteSize() == csz) {
										changed |= lhs.state.add(reshapeTo);
									} else {
										errorLog.put(v.getPointerKey(), new ReshapeError(t, reshapeTo));
									}
								}
							}
							return changed? CHANGED_AND_FIXED: NOT_CHANGED;
						}

						@Override
						public int hashCode() {
							return reshapeTo.hashCode();
						}

						@Override
						public boolean equals(Object o) {
							return this == o || ((o instanceof ReshapeOp) && reshapeTo.equals(((ReshapeOp)o).reshapeTo));
						}

						@Override
						public String toString() {
							return "reshape to " + reshapeTo;
						}
					}
					
					private final UnaryOperator<TensorVariable> nodeOp = new UnaryOperator<TensorVariable>() {
						@Override
						public byte evaluate(TensorVariable lhs, TensorVariable rhs) {
							if (rhs != null && rhs.state != null) {
								if (lhs == null || lhs.state == null) {
									lhs.copyState(rhs);
									return CHANGED;
								} else {
									return lhs.state.addAll(rhs.state)? CHANGED: NOT_CHANGED;
								}
							} else {
								return NOT_CHANGED;
							}
						}

						@Override
						public int hashCode() {
							return 817504253;
						}

						@Override
						public boolean equals(Object o) {
							return o == this;
						}

						@Override
						public String toString() {
							return "propagate node tensor types";
						}
						
					};

					@Override
					public UnaryOperator<TensorVariable> getNodeTransferFunction(PointsToSetVariable node) {
						if (reshapeNodes.containsKey(node)) {
							return new ReshapeOp(reshapeNodes.get(node), node);
						} else if (conv2ds.contains(node)) {
							return new ConvOp(2, node);
						} else if (conv3ds.contains(node)) {
							return new ConvOp(3, node);
						} else {
							return nodeOp;
						}
					}

					@Override
					public boolean hasNodeTransferFunctions() {
						return true;
					}

					@Override
					public UnaryOperator<TensorVariable> getEdgeTransferFunction(PointsToSetVariable src,
							PointsToSetVariable dst) {
						if (set_shapes.containsKey(dst)) {
							return new SetShapeOp(set_shapes.get(dst));
						} else {
							return nodeOp;
						}
					}

					@Override
					public boolean hasEdgeTransferFunctions() {
						return true;
					}

					@Override
					public AbstractMeetOperator<TensorVariable> getMeetOperator() {
						return new AbstractMeetOperator<TensorVariable>() {

							@Override
							public byte evaluate(TensorVariable lhs, TensorVariable[] rhs) {
								boolean changed = false;
								for(TensorVariable r : rhs) {
									changed |= lhs.state.addAll(r.state);
								}
								return changed? CHANGED: NOT_CHANGED;
							}

							@Override
							public int hashCode() {
								return 413158523;
							}

							@Override
							public boolean equals(Object o) {
								return this == o;
							}

							@Override
							public String toString() {
								return "Tensor types set union";
							}
						};
					}		
				};
			}	
		};
	}

	private final Map<PointsToSetVariable, TensorType> init;
		
	public TensorTypeAnalysis(Graph<PointsToSetVariable> G, 
			Map<PointsToSetVariable, TensorType> init, 
			Map<PointsToSetVariable, TensorType> reshapeTypes, 
			Map<PointsToSetVariable, TensorType> set_shapes,
			Set<PointsToSetVariable> conv2ds, 
			Set<PointsToSetVariable> conv3ds, 
			Map<PointerKey, AnalysisError> errorLog) {
		super(createProblem(G, reshapeTypes, set_shapes, conv2ds, conv3ds, errorLog));
		this.init = init;
	}
	
	@Override
	protected TensorVariable makeNodeVariable(PointsToSetVariable n, boolean IN) {
		return new TensorVariable();
	}

	@Override
	protected TensorVariable makeEdgeVariable(PointsToSetVariable src, PointsToSetVariable dst) {
		return new TensorVariable();
	}

	@Override
	protected TensorVariable[] makeStmtRHS(int size) {
		return new TensorVariable[size];
	}

	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		for(PointsToSetVariable src : init.keySet()) {
			getOut(src).state.add(init.get(src));
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("answer:\n");
		for(PointsToSetVariable var : getProblem().getFlowGraph()) {
			if (getOut(var) != null && getOut(var).state != null && !getOut(var).state.isEmpty()) {
				sb.append(var.getPointerKey()).append(getOut(var)).append("\n");
			}
		}
		return sb.toString();
	}
}
