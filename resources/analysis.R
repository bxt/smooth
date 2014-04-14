
# Load the df
df = read.table("analysis.txt", header = TRUE)

# Some basic properties of the df
names(df)
nrow(df)
sum(df$vertexCount)
mean(df$vertexCount)
sum(df$edgeCount)
mean(df$edgeCount)

# Percentage of graphs between 11 and 20 nodes:
(sum(df$vertexCount <= 20) - sum(df$vertexCount < 11)) / nrow(df)

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

overviewColramp <- c("red", "brown", "cyan", "blue")
pdf(file="area_comparison.pdf", width=7, height=9, colormodel='cmyk')
par(fig=c(0,1,0,0.8))
plot(aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, df, mean), type="n", xlab="Knotenanzahl", ylab="Durchschnittliche Fläche")
grid()
text(10, 5500, "(b)")
myplot(5, overviewColramp[1], aggregate((smoothAllAdjWidth* smoothAllAdjHeight) ~ vertexCount, df, mean))
myplot(0.3, overviewColramp[2], aggregate((width * height) ~ vertexCount, bst, mean))
myplot(0, overviewColramp[3], aggregate((orthogonalWidth* orthogonalHeight) ~ vertexCount, df, mean))
myplot(-0.3, overviewColramp[4], aggregate((orthogonalCompressedWidth* orthogonalCompressedHeight) ~ vertexCount, df, mean))

legend(10,5300, c("glatt-orthogonal, alle Anpassungen", "glatt-orthogonal, nötige Anpassungen", "orthogonal", "orthogonal, ohne S-Kanten"),lty=1,lwd=2.5,col=overviewColramp, box.col = "white", bg = "white")

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

# Plot space vs. node count for all methods

opacity <- 0.7

noop <- function(...) invisible(NULL)
myplot2 <- function(agg, filename, color=rgb(0,0,0,opacity), postpro=noop, colormodel='gray'){
pdf(file=filename, width=7, height=4.5, colormodel=colormodel)
par(mar=c(4,4,0.3,3))
plot(x=jitter(agg[,1]),y=agg[,2], xlab="Knotenanzahl", ylab="Fläche", col=color, pch=4, cex=.3, log="xy", ylim=c(9,10000))
lines(counts, (counts+1)^2, col=rgb(0,0,0,0.3))
lines(counts, (sqrt(counts)+1)^2, col=rgb(0,0,0,0.3))
grid()
postpro()
dev.off()
}

myplot2(data.frame(c(df$vertexCount),c(df$orthogonalWidth*df$orthogonalHeight)), "area_orthogonal.pdf")
myplot2(data.frame(c(df$vertexCount),c(df$orthogonalCompressedWidth*df$orthogonalCompressedHeight)), "area_orthogonal_compressed.pdf")
myplot2(data.frame(c(df$vertexCount),c(df$smoothAllAdjWidth*df$smoothAllAdjHeight)), "area_smooth.pdf")

colormap <- c(A=rgb(0,0,0,opacity), B=rgb(0,1,1,opacity), C=rgb(1,0,0,opacity))
aggBst <- data.frame(c(bst$vertexCount),c(bst$width*bst$height),c(bst$level))
drawBstLegend <- function() legend(10,11000, c("Keine Anpassungen nötig", "Steigung muss nicht korrigiert werden", "Alle Anpassungen nötig"), pch=4,col=colormap, box.col = "white")
myplot2(aggBst, "area_smooth_bst.pdf", colormap[aggBst[,3]], drawBstLegend, colormodel='cmyk')
