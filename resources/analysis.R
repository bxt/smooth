
# Load the df
df = read.table("analysis.txt", header = TRUE)

# Some basic properties of the df
names(df)
nrow(df)
sum(df$vertexCount)
mean(df$vertexCount)
sum(df$edgeCount)
mean(df$edgeCount)

# Theoretical maximum avg size per vertex, orthogonal:
mean((df$vertexCount+1)*(df$vertexCount+1)/(df$vertexCount))

# Avg size per vertex, orthogonal
mean((df$orthogonalHeight * df$orthogonalWidth) / df$vertexCount)

# Avg size per vertex, orthogonal with s-edges removed
mean((df$orthogonalCompressedHeight * df$orthogonalCompressedWidth) / df$vertexCount)

# Avg size per vertex and avg segment count, smooth-orthogonal
mean((df$smoothAllAdjHeight * df$smoothAllAdjWidth) / df$vertexCount)
sum(df$smoothAllAdjComplexity) / sum(df$edgeCount)

# Escalation levels
length(df$smoothComplexity[df$smoothComplexity >= 0])
length(df$smoothComplexity[df$smoothComplexity < 0])
length(df$smoothCheapAdjComplexity[df$smoothCheapAdjComplexity < 0])

# Select the best escalation level for each graph
escA <- df[df$smoothComplexity >= 0,c("filename", "smoothWidth", "smoothHeight", "smoothComplexity", "vertexCount")]
escB <- df[df$smoothComplexity < 0 & df$smoothCheapAdjComplexity >= 0,c("filename", "smoothCheapAdjWidth", "smoothCheapAdjHeight", "smoothCheapAdjComplexity", "vertexCount")]
escC <- df[df$smoothCheapAdjComplexity < 0,c("filename", "smoothAllAdjWidth", "smoothAllAdjHeight", "smoothAllAdjComplexity", "vertexCount")]
colnames(escA) <- c("filename", "width", "height", "complexity", "vertexCount")
escA$level <- "A"
colnames(escB) <- c("filename", "width", "height", "complexity", "vertexCount")
escB$level <- "B"
colnames(escC) <- c("filename", "width", "height", "complexity", "vertexCount")
escC$level <- "C"
bst <- rbind(escA, escB, escC)

# Avg size per vertex and avg segment count, smooth-orthogonal, optimal escalation level
mean((bst$width * bst$height) / bst$vertexCount)
sum(bst$complexity) / sum(df$edgeCount)

# Used graph vertex counts
counts <- rle(sort(df$vertexCount))$values

# Plot the space efficiency of all graphs

myplot <- function(offest, color, stuff){
lines(stuff, col = color, type="o", pch=4)
}

pdf(file="area_comparison.pdf", width=7, height=9, colormodel='cmyk')
par(fig=c(0,1,0,0.8))
plot(aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, df, mean), type="n", xlab="Knotenanzahl", ylab="Durchschnittliche Fläche")
grid()
text(10, 5500, "(b)")
myplot(5, "red", aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, df, mean))
myplot(0.3, "darkviolet", aggregate((width * height) ~ vertexCount, bst, mean))
myplot(0, "blue", aggregate((orthogonalWidth* orthogonalHeight) ~ vertexCount, df, mean))
myplot(-0.3, "brown", aggregate((orthogonalCompressedWidth* orthogonalCompressedHeight) ~ vertexCount, df, mean))

legend(10,5300, c("glatt-orthogonal, alle Anpassungen", "glatt-orthogonal, nötige Anpassungen", "orthogonal", "orthogonal, ohne S-Kanten"),lty=1,lwd=2.5,col=c("red", "darkviolet", "blue", "brown"), box.col = "white", bg = "white")

par(fig=c(0,1,0.6,1),new=TRUE)
plot(table(df$vertexCount), xaxt='n', ylab="Anzahl Graphen")
grid(ny=NA)
text(10, 80, "(a)")
dev.off()

# Show ten worst drawings:
tail(df[with(df,order((smoothAllAdjWidth*smoothAllAdjHeight)/vertexCount)),], n=10)

# Show ten worst drawings with best esc. lvl:
tail(bst[with(bst,order((width*height)/vertexCount)),], n=10)

# Show ten best liu-compressed drawings:
head(df[with(df,order( orthogonalCompressedHeight/orthogonalHeight )),], n=10)

# Show info about one special graph:
df[df$filename == "grafo7046",]

myplot2(data.frame(c(df$vertexCount),c(df$orthogonalWidth*df$orthogonalHeight)))
myplot2(data.frame(c(df$vertexCount),c(df$orthogonalCompressedWidth*df$orthogonalCompressedHeight)))
myplot2(data.frame(c(df$vertexCount),c(df$smoothAllAdjWidth*df$smoothAllAdjHeight)))

myplot2 <- function(agg, filename){
cnt <- aggregate(rep(1, nrow(agg)), agg, sum)
#pdf(file=filename, width=7, height=3.5, colormodel='gray')
#symbols(x=cnt[,1],y=cnt[,2],circles=sqrt(cnt[,3]/pi)/100, inches=FALSE, xlab="Knotenanzahl", ylab="Fläche", bg=rgb(0,0,0,0.5), fg=NULL, log="xy", ylim=c(5,max(cnt[,2])))
plot(x=jitter(cnt[,1]),y=cnt[,2], xlab="Knotenanzahl", ylab="Fläche", col=rgb(0,0,0,0.5), pch=4, log="xy", ylim=c(9,10000))
grid()
#dev.off()
}

colormap <- c(A="black", B="red", C="blue")
agg <- data.frame(c(bst$vertexCount),c(bst$width*bst$height))
cnt <- aggregate(rep(1, nrow(agg)), agg, sum)
#pdf(file=filename, width=7, height=3.5, colormodel='gray')
#symbols(x=cnt[,1],y=cnt[,2],circles=sqrt(cnt[,3]/pi)/100, inches=FALSE, xlab="Knotenanzahl", ylab="Fläche", bg=rgb(0,0,0,0.5), fg=NULL, log="xy", ylim=c(5,max(cnt[,2])))
plot(x=jitter(cnt[,1]),y=cnt[,2], xlab="Knotenanzahl", ylab="Fläche", col=colormap[], pch=4, log="xy", ylim=c(9,10000))
grid()
#dev.off()



