from itertools import permutations

with open('day8helper.txt', 'w+') as f:
    for perm in permutations("abcdefg"):
        f.write("".join(perm) + '\n')
        
