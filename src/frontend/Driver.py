import jpype as java
import os
from MainMenu import runMainMenu
from Authentication import authenticate


TESTING = True

path = os.getcwd()
path += "\\testing\\" if TESTING else "\\"
java.startJVM(classpath="build/backend.jar")

Initializer = java.JClass("src.backend.Initializer")
Initializer.main(path)

UserManager = java.JClass("src.backend.UserManager")
User = java.JClass("src.backend.User")
Vault = java.JClass("src.backend.Vault")
FileHandler = java.JClass("src.backend.FileHandler")

userManager = UserManager()

rerun = True
while rerun:
    user = authenticate(userManager, User, True)
    if user is None:
        break
    rerun = runMainMenu(Vault, User, FileHandler, user, userManager, path)