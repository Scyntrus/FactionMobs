import os

search = ""
replace = ""

def traverse_folder(directory):
    files = os.listdir(directory)
    for fname in files:
        fname = os.path.join(directory, fname)
        if os.path.isfile(fname):
            if (fname.endswith(".java") or fname.endswith(".txt") or fname.endswith(".yml")):
                f = open(fname, "rb")
                contents = "".join(f.readlines())
                f.close()
                if search in contents:
                    contents = contents.replace(search, replace)
                    f = open(fname, "wb")
                    f.write(contents)
                    f.close()
                    print fname
        elif os.path.isdir(fname):
            traverse_folder(fname)
    

print "------------------------------"
print
f1 = open("CB_package_version.txt", "rb")
search = f1.readline()
f1.close()
print "Old version number:", search
replace = raw_input("Enter new version number: ")
print
cwd = os.getcwd()
traverse_folder(cwd)
print
raw_input("Script finished. Press enter to close.")
