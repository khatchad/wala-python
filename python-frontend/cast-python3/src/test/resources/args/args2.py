# coding=utf-8
# 优先做这个, threading原型
def func2(f3, arg3,a4,a5,a6,a7):
    f3(arg3,a4,a5)
    return arg3


def func3(arg3,a2,a3):
    # print(arg3)
    return arg3


r_args = [func3, "a3"]
func2(*r_args)
