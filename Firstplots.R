# Figur 1 - 4

#Figur 1
runs <- c(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
time_taken <- c(0.660790, 0.661175, 0.557329, 0.543740, 0.542802, 0.553452, 0.548347, 0.563096, 0.558516, 0.554964)


# Create a data frame
data <- data.frame(Run = runs, Time = time_taken)


# Find the minimum and maximum y values to set the limits
min_y <- min(time_taken)
max_y <- max(time_taken)

# Plot the graph
plot(data$Run, data$Time, 
     type = "b",  # Both points and lines
     xlab = "Run Number",
     ylab = "Time Taken (seconds)",
     main = "Search parameters R = 10, W = 0",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, max_y * 1.1))  # Set y-axis limits to zoom in slightly

#Figur 2
runs <- c(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
time_taken <- c(0.560613, 0.546230, 0.539421, 0.547647, 0.548046, 0.534922, 0.544242, 0.556604, 0.556736, 0.549296)

# Create a data frame
data <- data.frame(Run = runs, Time = time_taken)


# Find the minimum and maximum y values to set the limits
min_y <- min(time_taken)
max_y <- max(time_taken)

# Plot the graph
plot(data$Run, data$Time, 
     type = "b",  # Both points and lines
     xlab = "Run Number",
     ylab = "Time Taken (seconds)",
     main = "Search Parameters R = 10, W = 2",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, max_y * 1.1))  # Set y-axis limits to zoom in slightly


#Figur 3
runs <- c(5, 10, 15, 20, 25, 30)
time_taken <- c(0.9987605582694478, 0.9876909300097844, 0.9956608889483511, 1.002690616120719, 1.006393504113481, 0.9938562872138282)
# Create a data frame
data <- data.frame(Run = runs, Time = time_taken)


# Find the minimum and maximum y values to set the limits
min_y <- min(time_taken)
max_y <- max(time_taken)

# Plot the graph
plot(data$Run, data$Time, 
     type = "b",  # Both points and lines
     xlab = "Number of Tasks",
     ylab = "Average Speedup",
     main = "Average speedup per number of task",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, max_y * 1.1))  # Set y-axis limits to zoom in slightly



#Figur 4
tasks <- c(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
average_speedups <- c(0.9990865203716257, 1.9601258284737426, 2.6517997413780234, 3.1453609961245435, 3.5217193786590406, 4.348759162104267, 5.000340723553035, 5.1748394306626295, 5.710124791969634, 5.985184724368407, 6.317090034082443, 6.62569055935551, 5.793965463367269, 5.934486818584351, 6.0976198101098475, 6.023243642095963, 6.286700317464327, 6.287550790897958, 6.404753607066754, 6.314626000797497)

# Create a data frame
speedup_data <- data.frame(Tasks = tasks, Speedup = average_speedups)

min_y <- min(speedup_data)
max_y <- max(speedup_data)

# Plot the graph
plot(speedup_data$Tasks, speedup_data$Speedup, 
     type = "b",  # Both points and lines
     xlab = "Number of tasks",
     ylab = "Average Speedup",
     main = "Average Speedup per number of task with -EC",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, 8))  # Set y-axis limits to zoom in slightly


#Figur 5
tasks <-  c(500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000, 8500, 9000, 9500, 10000)
average_speedups <- c(6.799359383062113, 7.654697875099361, 7.1720058377733995, 7.326795671227302, 7.5496140016204825, 
                      7.060598132616516, 7.002087747643056, 7.045000035332007, 6.605445012030275, 6.53247041222088, 
                      6.55237979315247, 6.7056134499293485, 6.5408894574036776, 5.681059774685889, 6.159431466387851, 
                      4.849636021071692, 5.940407649037768, 5.165300576133607, 5.641159033767562, 5.364351232888967)


# Create a data frame
speedup_data <- data.frame(Tasks = tasks, Speedup = average_speedups)

min_y <- min(speedup_data)
max_y <- max(speedup_data)

# Plot the graph
plot(speedup_data$Tasks, speedup_data$Speedup, 
     type = "b",  # Both points and lines
     xlab = "Number of tasks",
     ylab = "Average Speedup",
     main = "Average Speedup per large number of tasks",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, 9))  # Set y-axis limits to zoom in slightly



tasks <- c(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65)
average_speedups <- c(2.3555337598080865, 2.6084486832665257, 2.7583347757764587, 3.4321835443037974, 2.7395704968832195,
                      2.91865468088425, 2.0952399480832784, 2.663905987026778, 2.4957292669978988, 2.65622415434763,
                      2.0490325723830733, 2.5127746853088717, 2.7455080681096518)


# Create a data frame
speedup_data <- data.frame(Tasks = tasks, Speedup = average_speedups)

min_y <- min(speedup_data)
max_y <- max(speedup_data)

# Plot the graph
plot(speedup_data$Tasks, speedup_data$Speedup, 
     type = "b",  # Both points and lines
     xlab = "Number of tasks",
     ylab = "Average Speedup",
     main = "Average Speedup per number of task",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, 5))  # Set y-axis limits to zoom in slightly

