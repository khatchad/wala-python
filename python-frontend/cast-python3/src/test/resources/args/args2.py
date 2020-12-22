# coding=utf-8
# 优先做这个, threading原型
def func2(f3, arg3):
    f3(arg3)
    return arg3


def func3(arg3):
    print(arg3)
    return arg3


r_args = [func3, "a3"]
kw_args = {"func": func3, "arg3": "a4"}
func2(*r_args, **kw_args)
