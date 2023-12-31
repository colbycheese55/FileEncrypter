import jpype as java
import os
from MainMenu import MainMenu


TESTING = True

path = os.getcwd()
path += "\\testing\\" if TESTING else "\\"
java.startJVM(classpath="build/backend.jar")

Initializer = java.JClass("src.backend.Initializer")
Initializer.main(path)

UserManager = java.JClass("src.backend.UserManager")
userManager = UserManager()

username = input("Username: ")
password = input("Password: ")

User = java.JClass("src.backend.User")
user = User.authenticate(username, password, userManager)

Vault = java.JClass("src.backend.Vault")
vault = Vault(userManager, user)

FileHandler = java.JClass("src.backend.FileHandler")

rerun = True
while rerun:
    menu = MainMenu(vault, FileHandler)
    rerun = menu.openMenu()
print("DONE")