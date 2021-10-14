import matplotlib.pyplot as plt
import numpy as np
from scipy.interpolate import spline
from decimal import Decimal 

num_of_line = 0
x = []
y = []
x_min = 1

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/src/assignment1/momen_0.9_bestResult_bipolarRep_false.txt", "r") as f:
    for line in f.readlines():
        line = line.strip('\n') #去掉列表中每一个元素的换行符
        # x[num_of_line] = num_of_line
        # y[num_of_line] = Decimal(line)
        x.append(num_of_line)
    
        y.append(float(line))
        num_of_line += 1

x_max = num_of_line 


x = np.array(x)
y = np.array(y)
print(num_of_line)
print(len(x))

plt.plot(x, y, linewidth=1,c='blue')
plt.xlim((0, num_of_line+50))
plt.ylim((0, y.max()+0.02))
my_y_ticks = np.arange(0, y.max()+0.02, 0.05)
plt.yticks(my_y_ticks)
my_x_ticks = np.arange(0,num_of_line+50, 25)
plt.xticks(my_x_ticks)

plt.title("figure one for (c) binary rep", fontsize=16)
plt.xlabel("the number of epoch", fontsize=14)
plt.ylabel("total error", fontsize=14)
plt.tick_params(axis='both', labelsize=14)

plt.show()