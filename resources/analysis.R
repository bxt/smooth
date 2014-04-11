
# Load the data
data = read.table("analysis.txt", header = TRUE)

# Some basic properties of the data
names(data)
nrow(data)
sum(data$vertexCount)
mean(data$vertexCount)
sum(data$edgeCount)
mean(data$edgeCount)

# Theoretical maximum avg size per vertex, orthogonal:
mean((data$vertexCount+1)*(data$vertexCount+1)/(data$vertexCount))

# Avg size per vertex, orthogonal
mean((data$orthogonalHeight * data$orthogonalWidth) / data$vertexCount)

# Avg size per vertex, orthogonal with s-edges removed
mean((data$orthogonalCompressedHeight * data$orthogonalCompressedWidth) / data$vertexCount)

# Avg size per vertex and avg segment count, smooth-orthogonal
mean((data$smoothAllAdjHeight * data$smoothAllAdjWidth) / data$vertexCount)
sum(data$smoothAllAdjComplexity) / sum(data$edgeCount)

# Escalation levels
length(data$smoothComplexity[data$smoothComplexity >= 0])
length(data$smoothComplexity[data$smoothComplexity < 0])
length(data$smoothCheapAdjComplexity[data$smoothCheapAdjComplexity < 0])

# Select the best escalation level for each graph
escA <- data[data$smoothComplexity >= 0,c("filename", "smoothWidth", "smoothHeight", "smoothComplexity", "vertexCount")]
escB <- data[data$smoothComplexity < 0 & data$smoothCheapAdjComplexity >= 0,c("filename", "smoothCheapAdjWidth", "smoothCheapAdjHeight", "smoothCheapAdjComplexity", "vertexCount")]
escC <- data[data$smoothCheapAdjComplexity < 0,c("filename", "smoothAllAdjWidth", "smoothAllAdjHeight", "smoothAllAdjComplexity", "vertexCount")]
colnames(escA) <- c("filename", "width", "height", "complexity", "vertexCount")
colnames(escB) <- c("filename", "width", "height", "complexity", "vertexCount")
colnames(escC) <- c("filename", "width", "height", "complexity", "vertexCount")
bst <- rbind(escA, escB, escC)

# Avg size per vertex and avg segment count, smooth-orthogonal, optimal escalation level
mean((bst$width * bst$height) / bst$vertexCount)
sum(bst$complexity) / sum(data$edgeCount)

# Used graph vertex counts
counts <- rle(sort(data$vertexCount))$values

# Plot the space efficiency of all graphs

myplot <- function(offest, color, stuff){
lines(stuff, col = color, type="o", pch=4)
}

pdf(file="area_comparison.pdf", width=7, height=9)
par(fig=c(0,1,0,0.8))
plot(aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, data, mean), type="n", xlab="Knotenanzahl", ylab="Durchschnittliche Fläche")
grid()
text(10, 5500, "(b)")
myplot(5, "red", aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, data, mean))
myplot(0.3, "darkviolet", aggregate((width * height) ~ vertexCount, bst, mean))
myplot(0, "blue", aggregate((orthogonalWidth* orthogonalHeight) ~ vertexCount, data, mean))
myplot(-0.3, "brown", aggregate((orthogonalCompressedWidth* orthogonalCompressedHeight) ~ vertexCount, data, mean))

legend(10,5300, c("glatt-orthogonal, alle Anpassungen", "glatt-orthogonal, nötige Anpassungen", "orthogonal", "orthogonal, ohne S-Kanten"),lty=1,lwd=2.5,col=c("red", "darkviolet", "blue", "brown"), box.col = "white", bg = "white")

par(fig=c(0,1,0.6,1),new=TRUE)
plot(table(data$vertexCount), xaxt='n', ylab="Anzahl Graphen")
grid(ny=NA)
text(10, 80, "(a)")
dev.off()

# Show ten worst drawings:
tail(data[with(data,order((smoothAllAdjWidth*smoothAllAdjHeight)/vertexCount)),], n=10)

# Show ten worst drawings with best esc. lvl:
tail(bst[with(bst,order((width*height)/vertexCount)),], n=10)

# Show ten best liu-compressed drawings:
head(data[with(data,order( orthogonalCompressedHeight/orthogonalHeight )),], n=10)

# Show info about one special graph:
data[data$filename == "grafo7046",]