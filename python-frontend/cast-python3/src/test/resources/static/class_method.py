class A:
    def __init__(self, func):
        self.func = func

    @staticmethod
    def static_func1(func1, arg2):
        return func1(arg2)

    @classmethod
    def static_func2(cls, func1, arg2):
        return func1(arg2)

    def func3(self, arg):
        return self.func(arg)


def func(arg):
    print("func", arg)



A.static_func1(func, "a")
A.static_func2(func, "a")
#
# a = A(func)
# a.static_func1(func, "a")
# a.static_func2(func, "a")
# a.func3("a")
