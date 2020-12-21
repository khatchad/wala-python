def func2(f3, arg3):
    f3(arg3)
    return arg3


def func3(arg3):
    print(arg3)
    return arg3


kw_args = {"f3": func3, "arg3": "a3"}

func2(**kw_args)
