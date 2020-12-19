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


    def __init__(self, func):
        self.func = func

    @staticmethod
    def static_func1(func2, arg3):
        return func2(arg3)

    @classmethod
    def class_func2(cls, func3, arg4):
        # cls.static_func1(func3, arg4)
        return func3(arg4)

    def func3(self, arg3):
        return self.func(arg3)


def func(arg):
    print("func", arg)



# A.static_func1(func, "a") # yes
# A.class_func2(func, "a") # no func3 in ssa is v3 but func is v2
A.B.C.func4()
#
# a = A(func)
# a.static_func1(func, "a") # no
# a.class_func2(func, "a") # yes
# a.func3("a") # yes
