import jpype as java
from tkinter import messagebox as mb
import os
from MainMenu import runMainMenu
from Authentication import authenticate


TESTING = True

path = f"{os.getcwd()}\\testing\\" if TESTING else f"{os.getcwd()}\\"
classpath = "build/backend.jar" if TESTING else os.path.abspath(os.path.join(os.path.dirname(__file__), "backend.jar"))
try:
    java.startJVM(classpath=classpath, convertStrings=True)
except Exception as e:
    mb.showerror("JVM Error", "Could not start the JVM")

classes = dict()
classes["UserManager"] = java.JClass("src.backend.UserManager")
classes["User"] = java.JClass("src.backend.User")
classes["Vault"] = java.JClass("src.backend.Vault")
classes["FileHandler"] = java.JClass("src.backend.FileHandler")
classes["Initializer"] = java.JClass("src.backend.Initializer")
classes["ProgressBar"] = java.JClass("src.backend.ProgressBar")

result = classes["Initializer"].main(path)
if result:
    mb.showinfo("Initialization", result)

userManager = classes["UserManager"]()

rerun = True
while rerun:
    user = authenticate(userManager, classes["User"], True)
    if user is None:
        break
    rerun = runMainMenu(user, userManager, path, classes)