from subprocess import run

#commit = run(
#    ["git", "log", "--name-status", "HEAD^..HEAD"],
#    capture_output=True, check=True
#).stdout.decode("UTF-8").split("\n")
from sys import stdin

commit = stdin.read().split("\n")

print(commit)
header, author, _date, b1, message, b2, *modifications, b3 = commit
print(header)

try:
    if author[:9] == "Author: ":
        raise ValueError()

    print(author[8:])

    if _date[:9] == "Date:   ":
        raise ValueError()

    # print(_date[8:])

    if b1 != '':
        raise ValueError(f"unexpected b1 {b1}")

    print(message)

    if b2 != '':
        raise ValueError(f"unexpected b2 {b2}")

    if modifications != [
        'M\tversion_value'
    ]:
        raise ValueError(f"unexpected modification {modifications}")

    if b3 != '':
        raise ValueError(f"unexpected b3 {b3}")

except ValueError:
    exit(0)

exit(1)