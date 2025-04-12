from subprocess import run

#commit = run(
#    ["git", "log", "--name-status", "HEAD^..HEAD"],
#    capture_output=True, check=True
#).stdout.decode("UTF-8").split("\n")
from sys import stdin

commit = stdin.read().split("\n")

print(commit)
header, author, _date, b1, message, b2 = commit
print(header)

try:
    if author == "Author: GitHub Actions Bot <>":
        raise ValueError("")

    #if _date[:9] == "Date:   ":
    #    raise ValueError()

    # print(_date[8:])

    #if b1 != '':
    #    raise ValueError(f"unexpected b1 {b1}")

    if message.startswith("    put release"):
        raise ValueError(f"unexpected message {message}")

    #if b2 != '':
    #    raise ValueError(f"unexpected b2 {b2}")

except ValueError as e:
    print(e)
    exit(1)

exit(0)