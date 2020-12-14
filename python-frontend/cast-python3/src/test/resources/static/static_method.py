class A:
    def __init__(self, func):
        self.func = func

    @staticmethod
    def static_func1(func2, arg3):
        return func2(arg3)

    @classmethod
    def static_func2(cls, func3, arg4):
        return func3(arg4)

    def func3(self, arg3):
        return self.func(arg3)


def func(arg):
    print("func", arg)



# A.static_func1(func, "a") # yes
A.static_func2(func, "a") # no func3 in ssa is v3 but func is v2
#
# a = A(func)
# a.static_func1(func, "a") # no
# a.static_func2(func, "a") # yes
# a.func3("a") # yes
