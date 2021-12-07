import matplotlib.pyplot as plt
import numpy as np
from scipy.interpolate import spline
from decimal import Decimal 

num_of_line = 0
x = [0]
y = [0]

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/target/classes/Robot/MyTankRob.data/myTankRobot-logfile_1_a.txt", "r") as f:
    idx = 1
    for line in f.readlines():
        line = line.strip('\n') #去掉列表中每一个元素的换行符
        # x[num_of_line] = num_of_line
        # y[num_of_line] = Decimal(line)
        # if idx<9:
        #     continue
        # else:
        if line == "":
            continue
        if idx >=8 :
            subline = line.split(" ")
            x.append(num_of_line)
            y.append(float(subline[-1]))
            num_of_line += 1
        
        idx += 1

num_of_line_0 = 0
x_0 = [0]
y_0 = [0]

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/target/classes/Robot/MyTankRob.data/myTankRobot-logfile_3_off_policy_0.0.txt", "r") as f:
    idx_0 = 1
    for line in f.readlines():
        line = line.strip('\n') #去掉列表中每一个元素的换行符
        # x[num_of_line] = num_of_line
        # y[num_of_line] = Decimal(line)
        # if idx<9:
        #     continue
        # else:
        if line == "":
            continue
        if idx_0 >=8 :
            subline = line.split(" ")
            x_0.append(num_of_line_0)
            y_0.append(float(subline[-1]))
            print(float(subline[-1]))
            # print(idx)
            num_of_line_0 += 1
        
        idx_0 += 1


num_of_line_2 = 0
x_2 = [0]
y_2 = [0]
with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/target/classes/Robot/MyTankRob.data/myTankRobot-logfile_3_off_policy_0.25.txt", "r") as f:
    idx_2 = 1
    for line in f.readlines():
        line = line.strip('\n') #去掉列表中每一个元素的换行符
        # x[num_of_line] = num_of_line
        # y[num_of_line] = Decimal(line)
        # if idx<9:
        #     continue
        # else:
        if line == "":
            continue
        if idx_2 >=8 and num_of_line_2<= 100:
            subline = line.split(" ")
            x_2.append(num_of_line_2)
            y_2.append(float(subline[-1]))
            # print(float(subline[-1]))
            # print(idx)
            num_of_line_2 += 1
        
        idx_2 += 1


num_of_line_3 = 0
x_3 = [0]
y_3 = [0]

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/target/classes/Robot/MyTankRob.data/myTankRobot-logfile_3_off_policy_0.75.txt", "r") as f:
    idx_3 = 1
    for line in f.readlines():
        line = line.strip('\n') #去掉列表中每一个元素的换行符
        # x[num_of_line] = num_of_line
        # y[num_of_line] = Decimal(line)
        # if idx<9:
        #     continue
        # else:
        if line == "":
            continue
        if idx_3 >=8 :
            subline = line.split(" ")
            x_3.append(num_of_line_3)
            y_3.append(float(subline[-1]))
            # print(float(subline[-1]))
            # print(idx_3)
            num_of_line_3 += 1
        
        idx_3 += 1






x_max = num_of_line 


x = np.array(x)
y = np.array(y)

x_2 = np.array(x_2)
y_2 = np.array(y_2)


x_3 = np.array(x_3)
y_3 = np.array(y_3)


x_0 = np.array(x_0)
y_0 = np.array(y_0)
# print(num_of_line_2)
# print(len(x_2))

plt.plot(x_0, y_0, linewidth=1,c='blue',label="e == 0.0")
plt.plot(x, y, linewidth=1,c='orange',label="e == 0.1")
plt.plot(x_2, y_2, linewidth=1,c='red',label="e == 0.25")
plt.plot(x_3, y_3, linewidth=1,c='green',label="e == 0.75")
plt.legend()
# plt.xlim((0, num_of_line))
# plt.ylim((0, y.max()))
my_y_ticks = np.arange(0, y.max(), 10)
plt.yticks(my_y_ticks)
my_x_ticks = np.arange(0,num_of_line, 10)
plt.xticks(my_x_ticks)

plt.title("2-(c): having intermediate rewards vs only terminal rewards", fontsize=16)
plt.xlabel("the number of #50 round", fontsize=14)
plt.ylabel("winning rate", fontsize=14)
# plt.tick_params(axis='both', labelsize=14)

plt.show()
