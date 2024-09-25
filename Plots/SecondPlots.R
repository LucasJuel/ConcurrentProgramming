library(ggplot2)
library(tidyr)
library(dplyr)

# Create the data frame
data <- data.frame(
  Tasks = rep(15:25, 5),
  Threads = rep(c(2, 4, 6, 8, 10), each = 11),
  Speedup = c(
    1.774628864, 1.558833876, 1.477203734, 1.660912439, 1.540433261, 1.670911675, 1.538068393, 1.60110446, 1.45889839, 1.569787713, 1.609235722,
    2.852881761, 2.494508673, 2.444699038, 2.675866353, 2.557024061, 2.531924658, 2.720227185, 2.595262633, 2.57157993, 2.741446268, 2.638388008,
    3.567899189, 2.748700277, 3.121251796, 2.913608403, 3.069244025, 3.119956935, 3.302092102, 3.531486414, 3.116805726, 3.643966991, 2.941926186,
    3.268148412, 3.481762347, 3.680165602, 3.460592309, 3.072145681, 3.848395471, 3.549375109, 3.320120203, 2.859886483, 3.224653908, 3.851262608,
    3.562206843, 3.426635965, 3.894642879, 3.802914313, 3.638496229, 3.327744015, 3.830113698, 3.639459543, 3.374249685, 3.348666469, 3.814377406
  )
)

# Create the plot
ggplot(data, aes(x = Tasks, y = Speedup, color = factor(Threads))) +
  geom_line() +
  geom_point() +
  scale_color_discrete(name = "Number of Threads") +
  labs(title = "Speedup vs Number of Tasks for Different Thread Counts",
       x = "Number of Tasks",
       y = "Average Speedup") +
  theme_minimal() +
  theme(legend.position = "right")
