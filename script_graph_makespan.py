
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 10 15:13:52 2022

@author: Juliette
"""

import matplotlib.pyplot as plt

#on crée des vecteurs dans lesquels on va rentrer le nombre d'étape de chaque plan avec chaque planner
HSPval = [6,8,8,19]
ASPval = [0,0,0,0] #notre planner ASP ne trouve pas de plan 
#et pour chaque benchmark
HSPproblem = ["blocks","gripper","depots","logistics"]
ASPproblem = ["blocks","gripper","depots","logistics"]

        
print(HSPval,ASPval,HSPproblem,ASPproblem)


#On génère le graph
 
plt.xlabel("Benchmark")
plt.ylabel("Makespan (number of steps)") 
plt.title("Makespan of ASP and HSP planners for each benchmark")

plt.plot(HSPproblem,HSPval, label = "HSP")
plt.plot(ASPproblem,ASPval, label = "ASP")


plt.savefig("graphMAkespan.png") #on enregistre l'image
plt.legend()
plt.show()


