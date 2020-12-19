class A:
    class B:
        @classmethod
        def func4(cls):
            cls.func5()
            return "func4"
        @classmethod
        def func5(cls):
            return "func5"
        class C:
            @classmethod
            def func4(cls):
                cls.func5()
                return "func4"
            @classmethod
            def func5(cls):
                return "func5"

    def __init__(self):
        pass


# A.static_func1(func, "a") # yes
# A.class_func2(func, "a") # no func3 in ssa is v3 but func is v2
A.B.C.func4()
#
# a = A(func)
# a.static_func1(func, "a") # no
# a.class_func2(func, "a") # yes
# a.func3("a") # yes
