import matplotlib.pyplot as plt
import numpy as np
from scipy.interpolate import spline
from decimal import Decimal 

num_of_line = 0
x = []
y = []

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/question4Res/lut_out_nH=12lR=0.001m=0.7.txt", "r") as f:
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
        if idx >=2 :
            subline = line.split("\t")
            x.append(num_of_line)
            y.append(float(subline[-1]))
            num_of_line += 1
            # print(float(subline[-1]))
        
        idx += 1

num_of_line_0 = 0
x_0 = []
y_0 = []

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/question4Res/lut_out_nH=18lR=0.001m=0.7.txt", "r") as f:
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
        if idx_0 >=2 :
            subline = line.split("\t")
            x_0.append(num_of_line_0)
            y_0.append(float(subline[-1]))
            # (float(subline[-1]))print
            # print(idx)
            num_of_line_0 += 1
        
        idx_0 += 1


num_of_line_2 = 0
x_2 = []
y_2 = []
with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/question4Res/lut_out_nH=30lR=0.001m=0.7.txt", "r") as f:
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
        if idx_2 >=2:
            subline = line.split("\t")
            x_2.append(num_of_line_2)
            y_2.append(float(subline[-1]))
            # print(float(subline[-1]))
            # print(idx)
            num_of_line_2 += 1
        
        idx_2 += 1


num_of_line_3 = 0
x_3 = []
y_3 = []

with open("/Users/jun/Desktop/UBC/2021W1/502/CPEN502/question4Res/lut_out_nH=42lR=0.001m=0.7.txt", "r") as f:
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
        if idx_3 >=2 :
            subline = line.split("\t")
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
plt.plot(x, y, linewidth=1,c='orange',label="hiddennum == 12")
plt.plot(x_0, y_0, linewidth=1,c='blue',label="hiddennum == 18")
plt.plot(x_2, y_2, linewidth=1,c='red',label="hiddennum == 30")
plt.plot(x_3, y_3, linewidth=1,c='green',label="hiddennum == 42")
plt.legend()
plt.xlim((0, x_3.max()+2000))
plt.ylim((0.05, y_3.max()))
my_y_ticks = np.arange(0.05, y_3.max(), 0.01)
plt.yticks(my_y_ticks)
my_x_ticks = np.arange(0, x_3.max()+2000, 1000)
plt.xticks(my_x_ticks)

plt.title("#hidden layer", fontsize=16)
plt.xlabel("the number of epoch", fontsize=14)
plt.ylabel("Error", fontsize=14)
plt.tick_params(axis='both', labelsize=14)

plt.show()
