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


counts <- rle(sort(data$vertexCount))$values


myplot <- function(offest, color, stuff){
lines(stuff, col = color, type="o", pch="+")
}

plot(aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, data, mean), type="n", xlab="Knotenanzahl", ylab="Durchschnittliche Fläche")
myplot(5, "red", aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, data, mean))
myplot(0.3, "green", aggregate((width * height) ~ vertexCount, bst, mean))
myplot(0, "blue", aggregate((orthogonalWidth* orthogonalHeight) ~ vertexCount, data, mean))
myplot(-0.3, "brown", aggregate((orthogonalCompressedWidth* orthogonalCompressedHeight) ~ vertexCount, data, mean))

legend(10,5500, c("glatt-orthogonal, nötige Anpassungen","glatt-orthogonal, alle Anpassungen", "orthogonal", "orthogonal, ohne S-Kanten"),lty=1,lwd=2.5,col=c("green","red", "blue", "brown"), box.col = "white")




addalhpa <- function(c, alpha) {
x <- col2rgb(c)/255
rgb(x[1], x[2], x[3], alpha=alpha)
}



