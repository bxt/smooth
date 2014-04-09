data = read.table("analysis.txt", header = TRUE)

names(data)
nrow(data)
sum(data$vertexCount)
mean(data$vertexCount)
sum(data$edgeCount)
mean(data$edgeCount)

mean((data$orthogonalHeight * data$orthogonalWidth) / data$vertexCount)

mean((data$orthogonalCompressedHeight * data$orthogonalCompressedWidth) / data$vertexCount)

mean((data$smoothAllAdjHeight * data$smoothAllAdjWidth) / data$vertexCount)
sum(data$smoothAllAdjComplexity) / sum(data$edgeCount)


length(data$smoothComplexity[data$smoothComplexity >= 0])
length(data$smoothComplexity[data$smoothComplexity < 0])
length(data$smoothCheapAdjComplexity[data$smoothCheapAdjComplexity < 0])


escA <- data[data$smoothComplexity >= 0,c("smoothWidth", "smoothHeight", "smoothComplexity", "vertexCount")]
escB <- data[data$smoothComplexity < 0 & data$smoothCheapAdjComplexity >= 0,c("smoothCheapAdjWidth", "smoothCheapAdjHeight", "smoothCheapAdjComplexity", "vertexCount")]
escC <- data[data$smoothCheapAdjComplexity < 0,c("smoothAllAdjWidth", "smoothAllAdjHeight", "smoothAllAdjComplexity", "vertexCount")]
colnames(escA) <- c("width", "height", "complexity", "vertexCount")
colnames(escB) <- c("width", "height", "complexity", "vertexCount")
colnames(escC) <- c("width", "height", "complexity", "vertexCount")
bst <- rbind(escA, escB, escC)

mean((bst$width * bst$height) / bst$vertexCount)
sum(bst$complexity) / sum(data$edgeCount)


plot(aggregate((width * height) ~ vertexCount, bst, mean))
