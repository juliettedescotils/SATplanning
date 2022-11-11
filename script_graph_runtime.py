# -*- coding: utf-8 -*-
"""
Created on Thu Nov 10 17:29:52 2022

@author: Juliette
"""

import matplotlib.pyplot as plt

#on crée des vecteurs dans lesquels on va rentrer les valeurs obtenues avec chaque planner
HSPval = [0.17,0.24,0.27,0.31]
ASPval = [0.16,0.22,0.23,0.17]
#et pour chaque benchmark
HSPproblem = ["blocksworld","depots","gripper","logistics"]
ASPproblem = ["blocksworld","depots","gripper","logistics"]

        
print(HSPval,ASPval,HSPproblem,ASPproblem)


#On génère le graph
 
plt.xlabel("Problem")
plt.ylabel("Resolution time") 
plt.title("Resolution time of ASP and HSP planners for each benchmark")

plt.plot(HSPproblem,HSPval, label = "HSP")
plt.plot(ASPproblem,ASPval, label = "ASP")


plt.savefig("graphRuntime.png") #on enregistre l'image
plt.legend()
plt.show()


