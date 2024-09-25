# Create data vectors
runs <- 0:9
time_taken <- c(0.020711, 0.024783, 0.024500, 0.018353, 0.032818, 0.018432, 0.018791, 0.019313, 0.018862, 0.025085)
c(0.020362, 0.026051, 0.024463, 0.018502, 0.032121, 
  0.018291, 0.018751, 0.018991, 0.019343, 0.026559)

runs <- 0:9
time_taken <- c(0.033958, 0.019807, 0.020169, 0.019318, 0.019341, 0.026663, 0.018242, 0.018353, 0.018101, 0.021363)

runs <- 0:19
time_taken <- c(0.032775, 0.019739, 0.019052, 0.019276, 0.019155, 0.025337, 0.018409, 0.018842, 0.019543, 0.018519, 0.028509, 0.017935, 0.018242, 0.018366, 0.018430, 0.042858, 0.018059, 0.018087, 0.019448, 0.018352)

runs <- 0:3
time_taken <- c(0.019504,0.019346,0.018611,0.019111)
time_taken <- c(0.023430, 0.019364, 0.026316, 0.019481)

time_taken <- c(0.022271, 0.019080, 0.020209, 0.019318) #Singletask sammenligning

time_taken <- c(0.010679, 0.006098, 0.007897, 0.006912) #Multitask

NoWarm <- sd(time_taken1)
Warm <- sd(time_taken2)
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
     main = "Keyword 'he' R = 10, W = 5",
     pch = 16,    # Point type
     col = "blue",
     ylim = c(0, max_y * 1.1))  # Set y-axis limits to zoom in slightly

# Add grid lines
grid()

# Add a horizontal line for the mean time
abline(h = mean(time_taken), col = "red", lty = 2)

# Add a legend
legend("topright", legend = c("Time", "Mean Time"), 
       col = c("blue", "red"), lty = c(1, 2), pch = c(16, NA))



tasks <- 1:20
average_speedups <- c(0.7922212691173087, 1.5601913361862536, 2.1450924807267877, 2.6216078344509106, 2.7014284287283994,
                      2.941355140186916, 2.9093393405879, 2.401384330844485, 2.7568787687876877, 2.6144781724838917,
                      2.9360095983779035, 2.7572045730863426, 2.536001736632936, 2.5753250594989985, 2.8165393292509524,
                      2.8206878178905566, 2.631292955251263, 2.8222149338580955, 3.0066116772724496, 2.9274302054951105)


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



tasks <- c(5, 10, 15, 20, 25, 30)
average_speedups <- c(0.9270308034157287, 0.8882412175306099, 0.8539974064559916, 
                      0.8984064439260377, 0.8457316978718791, 0.8705916277959068)


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
     ylim = c(0, 2))  # Set y-axis limits to zoom in slightly



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

