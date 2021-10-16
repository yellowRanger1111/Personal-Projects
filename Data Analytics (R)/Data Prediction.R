### Start of Assignment 2 Owen 30039096 ####
library(dplyr)
library(tree)
library(e1071)
library(xgboost)
library(randomForest)
library(rpart)
library(ROCR)
library(adabag)
library(ggplot2)
# library(neuralnet)




rm(list = ls())
WAUS <- read.csv("CloudPredict2021.csv")
L <- as.data.frame(c(1:49))
set.seed(30039096) # Your Student ID is the random seed
L <- L[sample(nrow(L), 10, replace = FALSE),] # sample 10 locations
WAUS <- WAUS[(WAUS$Location %in% L),]
WAUS <- WAUS[sample(nrow(WAUS), 2000, replace = FALSE),] # sample 2000 rows



###### QUESTION 1 #####

# summary
summary(WAUS)

# cloudy proportion
onecloud = nrow(WAUS %>% filter (CloudTomorrow == 1)) / nrow(WAUS)
onecloud
oneclear = nrow(WAUS %>% filter (CloudTomorrow == 0)) / nrow(WAUS)
oneclear
oneprop = onecloud / oneclear
oneprop

# oneambiguous = nrow(WAUS %>% filter (CloudTomorrow != 0 & CloudTomorrow !=1)) / nrow(WAUS)

# finding standard deviation 
onesd = sapply(WAUS,sd,na.rm=TRUE)
onesd



###### QUESTION 2 #####
#clearing all NA from all rows
trainingset = na.omit(WAUS)

# testing omitting few variable
# trainingset = trainingset %>% select(-Day, -Rainfall, -RainToday)
# trainingset = trainingset %>% select(-Humidity9am, -MinTemp,-RainToday, -Rainfall, -Temp9am)
# trainingset = trainingset %>% select(-Year, -Rainfall, -MinTemp, -Humidity9am)

# trainingset$WindGustDir = as.numeric(as.factor(trainingset$WindGustDir))
# trainingset$WindDir3pm = as.numeric(as.factor(trainingset$WindDir3pm))
# trainingset$WindDir9am = as.numeric(as.factor(trainingset$WindDir9am))




##### QUESTION 3 #####
train.row = sample(1:nrow(trainingset), 0.7*nrow(trainingset))
trained = trainingset[train.row,]
tested = trainingset[-train.row,]

# attach(trained)
# attach(tested)

##### QUESTION 4 #####
# so not trained as numeric, safety reason, kinda change to bool
trained$CloudTomorrow = as.factor(trained$CloudTomorrow)

# make tree
trytree = tree(CloudTomorrow~., data = trained)
plot(trytree) + text(trytree, pretty = 0, cex = 0.6)


# naive bayes
naivbayes = naiveBayes(CloudTomorrow~.,data = trained)


# bagging
tas = bagging(CloudTomorrow~.,data = trained, mfinal = 20)


# boosting
mabus = boosting(CloudTomorrow~.,data = trained, mfinal = 20)

# random forest
hutan = randomForest(CloudTomorrow~.,data = trained)



##### QUETSION 5 ######
# confusion matrix

# for tree
predicttree = predict(trytree, tested, type = "class")
combtree = table(tested$CloudTomorrow, predicttree)
# acuracy
accutree=round(
  (combtree[2,2]+combtree[1,1])/
  (combtree[2,2]+combtree[1,1]+combtree[2,1]+combtree[1,2]),
  2)



#for naives bayes
predictnb = predict(naivbayes, tested, type = "class")
combnb = table(tested$CloudTomorrow, predictnb)
# acuracy
accunb=round(
  (combnb[2,2]+combnb[1,1])/
  (combnb[2,2]+combnb[1,1]+combnb[2,1]+combnb[1,2]),
  2)


# for bagging
predictbag = predict(tas, tested, type = "class")
combbag = table(tested$CloudTomorrow, predictbag$class)
# acuracy
accubag=round(
  (combbag[2,2]+combbag[1,1])/
  (combbag[2,2]+combbag[1,1]+combbag[2,1]+combbag[1,2])
  ,2)


# for boosting
predictbus = predict(mabus, tested, type = "class")
combbus = table(tested$CloudTomorrow, predictbus$class)
# acuracy
accubus=round(
  (combbus[2,2]+combbus[1,1])/
  (combbus[2,2]+combbus[1,1]+combbus[2,1]+combbus[1,2])
  ,2)


# for random forest
predictrf = predict(hutan, tested, type = "class")
combrf = table(tested$CloudTomorrow, predictrf)
# acuracy
accurf=round(
  (combrf[2,2]+combrf[1,1])/
  (combrf[2,2]+combrf[1,1]+combrf[2,1]+combrf[1,2]),
  2)





###### QUESTION 6 #######
# confidence of predicting


# for tree
conftree = predict(trytree, tested, type = "vector")
predconftree = prediction(conftree[,2], tested$CloudTomorrow)
# tpr fpr
tftree = performance(predconftree,'tpr','fpr')
plot(tftree, col='red') + abline(0,1)
# auc 
auctree =	performance(predconftree,	"auc")
# plot(auctree)
auctreenum = round(as.numeric(auctree@y.values),2)


# for naive bayes
confnb = predict(naivbayes, tested, type = "raw")
predconfnb = prediction(confnb[,2], tested$CloudTomorrow)
# tpr frp
tfnb = performance(predconfnb, 'tpr', 'fpr')
plot(tfnb, col='blueviolet', add = TRUE) + abline(0,1)
# auc
aucnb = performance(predconfnb, 'auc')
aucnbnum = round(as.numeric(aucnb@y.values),2)


# for bagging
predconfbag = prediction(predictbag$prob[,2], tested$CloudTomorrow)
# tpr frp
tfbag = performance(predconfbag, 'tpr', 'fpr')
plot(tfbag, add = TRUE, col = 'darkgreen') + abline(0,1)
# auc 
aucbag = performance(predconfbag, 'auc')
aucbagnum = round(as.numeric(aucbag@y.values),2)


# for boosting
predconfbus = prediction(predictbus$prob[,2], tested$CloudTomorrow)
# tpr frp
tfbus = performance(predconfbus, 'tpr', 'fpr')
plot(tfbus, col = 'blue', add = TRUE) + abline(0,1)
# auc 
aucbus = performance(predconfbus, 'auc')
aucbusnum = round(as.numeric(aucbus@y.values),2)



# for random forest
confrf = predict(hutan, tested, type = "prob")
predconfrf = prediction(confrf[,2], tested$CloudTomorrow)
# tpr frp
tfrf = performance(predconfrf, 'tpr', 'fpr')
plot(tfrf, col = 'black', add = TRUE) + abline(0,1)
# auc 
aucrf = performance(predconfrf, 'auc')
aucrfnum = round(as.numeric(aucrf@y.values),2)

# plot roc and abline
plot(tftree, col='red') + 
plot(tfnb, col='blueviolet', add = TRUE) + 
plot(tfbag, add = TRUE, col = 'green') + 
plot(tfbus, col = 'blue', add = TRUE) + 
plot(tfrf, col = 'black', add = TRUE) + abline(0,1) +
legend(0,1,legend = c('Tree','Naive Bayes','Bagging','Boosting','Random Forest'),
       col=c('red','blueviolet','green','blue','black'), lty=1:2, cex=0.5)





###### QUESTION 7 ######
comparentech = data.frame("tech"= c("Decision Tree","Naive Bayes","Bagging","Boosting","Random Forest"), 
                            "AUC" = c(auctreenum, aucnbnum, aucbagnum, aucbusnum, aucrfnum),
                            "Accuracy" = c(accutree, accunb, accubag, accubus, accurf)
)





###### QUESTION 8 ######
summary(trytree)

importantvar = data.frame("Bagging" = round(tas$importance,2),
                          "Boosting" = round(mabus$importance,2),
                          "RandomForest" = round(hutan$importance,2))
colnames(importantvar) = c( "Bagging", "Boosting", "RandomForest")
importantvar = importantvar %>% mutate(total = Bagging +Boosting + RandomForest)


# "Tree" = c(0,5,10,13,6,0,9,8,2,3,12,0,1,0,0,0,0,0,11,7,0,4),
# tas$importance
# mabus$importance
# hutan$importance


###### QUESTION 9 ######
importantvar = arrange(importantvar, desc(total))
importantvar

ninetree = tree(CloudTomorrow~., data = trained %>% select(
  WindDir3pm, WindGustDir, WindDir9am, Sunshine, Humidity3pm, Pressure3pm, CloudTomorrow))

plot(ninetree) + text(ninetree, pretty = 0, cex = 0.8)

predninetree = predict(ninetree, tested, type = "class")
ninetreetable = table(tested$CloudTomorrow, predninetree)
accuracyninetree = round(
  (ninetreetable[2,2]+ninetreetable[1,1])/
    (ninetreetable[2,2]+ninetreetable[1,1]+ninetreetable[2,1]+ninetreetable[1,2]),
  2)


trained %>% filter(Sunshine <8,45 & Pressure3pm > 998.1)
nrow(tested %>% filter(Sunshine <8,45 & Pressure3pm > 998.1))
nrow(tested %>% filter(Sunshine <8,45 & Pressure3pm > 998.1 & CloudTomorrow == 1))

nrow(tested %>% filter(Sunshine <8,45 & Pressure3pm < 998.1))


###### QUESTION 10 ######
# tree 
testprune = cv.tree(trytree, FUN = prune.misclass)

# based on data above
tree20 = prune.misclass(trytree, best = 20)
tree19 = prune.misclass(trytree, best = 19)
tree18 = prune.misclass(trytree, best = 18)
tree11 = prune.misclass(trytree, best = 11)
tree5 = prune.misclass(trytree, best = 5)


# testing accuracy of all tree above
# 20
predicttree20 = predict(tree20, tested, type = "class")
combtree = table(tested$CloudTomorrow, predicttree20)
# acuracy
accutree20=round(
  (combtree[2,2]+combtree[1,1])/
    (combtree[2,2]+combtree[1,1]+combtree[2,1]+combtree[1,2]),
  2)


# 19
predicttree19 = predict(tree19, tested, type = "class")
combtree = table(tested$CloudTomorrow, predicttree19)
# acuracy
accutree19=round(
  (combtree[2,2]+combtree[1,1])/
    (combtree[2,2]+combtree[1,1]+combtree[2,1]+combtree[1,2]),
  2)


# 18
predicttree18 = predict(tree18, tested, type = "class")
combtree = table(tested$CloudTomorrow, predicttree18)
# acuracy
accutree18=round(
  (combtree[2,2]+combtree[1,1])/
    (combtree[2,2]+combtree[1,1]+combtree[2,1]+combtree[1,2]),
  2)


# 11
predicttree11 = predict(tree11, tested, type = "class")
combtree = table(tested$CloudTomorrow, predicttree11)
# acuracy
accutree11=round(
  (combtree[2,2]+combtree[1,1])/
    (combtree[2,2]+combtree[1,1]+combtree[2,1]+combtree[1,2]),
  2)


# 5
predicttree5 = predict(tree5, tested, type = "class")
combtree = table(tested$CloudTomorrow, predicttree5)
# acuracy
accutree5=round(
  (combtree[2,2]+combtree[1,1])/
    (combtree[2,2]+combtree[1,1]+combtree[2,1]+combtree[1,2]),
  2)


# accu table for pruned tree
accutabletree = data.frame(accutree20, accutree19, accutree18, accutree11, accutree5)


# from the data above, they say 5 is the best, we will try get 5 vs the original tree
besttree = prune.misclass(trytree, best = 5)


plot(besttree) + text(besttree, pretty = 0, cex = 0.7)


predictbesttree = predict(besttree, tested, type = "vector")
predbesttree = prediction(predictbesttree[,2], tested$CloudTomorrow)
#auc and roc
tfbesttree = performance(predbesttree, 'tpr', 'fpr')
aucbesttree = round(as.numeric(performance(predbesttree, 'auc')@y.values),2)


comptree = data.frame("original" = c(accutree, auctreenum), "new" = c(accutree5, aucbesttree))
rownames(comptree) = c("accuracy", "auc")
comptree


# roc graph
plot(tftree, col='red') + 
  plot (tfbesttree, col = "blue", add=TRUE) +
  abline(0,1) +
  legend (0,1, legend = c("Ori", "new"), col = c("red", "blue"), lty=1:2, cex=0.5)



# for bagging
# bagtest = bagging.cv(CloudTomorrow~., data = trainingset, v = 10, mfinal = 10, control)
bagbest = bagging(CloudTomorrow~., data = trained %>% select( -Rainfall,-RainToday,-Location), mfinal = 20)
bagbestpred = predict(bagbest, tested, type = "class")
combbagbest = table(tested$CloudTomorrow, bagbestpred$class)
accubagbest = round(
  (combbagbest[2,2]+combbagbest[1,1])/
    (combbagbest[2,2]+combbagbest[1,1]+combbagbest[2,1]+combbagbest[1,2]),
  2)

bagbestprediction = prediction(bagbestpred$prob[,2], tested$CloudTomorrow)
# AUC and ROC
aucbagbestnum = round(as.numeric(performance(bagbestprediction, 'auc')@y.values),2)



# boost
# boosttest = boosting.cv(CloudTomorrow ~., data = trainingset, boos = TRUE, mfinal = 10, coeflearn = "Breiman")
boostbest = boosting(CloudTomorrow~., data = trained %>% select( -Rainfall,-RainToday,-Location), mfinal = 20)
boostbestpred = predict(boostbest, tested, type = "class")
combbusbest = table(tested$CloudTomorrow, boostbestpred$class)
accuboostbest = round(
  (combbusbest[2,2]+combbusbest[1,1])/
    (combbusbest[2,2]+combbusbest[1,1]+combbusbest[2,1]+combbusbest[1,2]),
  2)

busbestprediction = prediction(boostbestpred$prob[,2], tested$CloudTomorrow)
# auc
aucbustbestnum = round(as.numeric(performance(busbestprediction, 'auc')@y.values),2)


# renew compare table
comparentechnew = rbind(comparentech, data.frame("tech" = c("NewTree", "NewBag", "NewBoost"), 
                                                 "AUC" = c(aucbesttree, aucbagbestnum,aucbustbestnum),
                                                 "Accuracy" = c(accutree5, accubagbest, accuboostbest)))



###### QUESTION 11 ######
library(neuralnet)

annset = trainingset
annset$WindGustDir = as.numeric(as.factor(annset$WindGustDir))
annset$WindDir3pm = as.numeric(as.factor(annset$WindDir3pm))
annset$WindDir9am = as.numeric(as.factor(annset$WindDir9am))
annset = annset %>% select(-RainToday)
# annset$CloudTomorrow = as.factor(annset$CloudTomorrow)
# summary(annset)

# for training 80% of the data, for tetsing 20%
set.seed(30039096)
abcd=sample(1:nrow(annset),0.8*nrow(annset))
annset.train = annset[abcd,]
annset.test = annset[-abcd,]

attach(annset.train)

# neuralnetwork = neuralnet(CloudTomorrow~ + Day + Month + Year  + Location + MinTemp + MaxTemp
#                           + Rainfall + Evaporation + Sunshine + WindGustDir + WindGustSpeed
#                           + WindDir3pm + WindDir9am + WindSpeed3pm + WindSpeed9am + Humidity3pm
#                           + Humidity9am + Pressure3pm + Pressure9am + Temp3pm + Temp9am
#                           , data = annset.train, hidden = 3)

set.seed(30039096)
neuralnetwork = neuralnet(CloudTomorrow~ Sunshine +  WindDir3pm + WindGustDir + WindDir9am
                          , data = annset.train %>% select(WindDir3pm, WindGustDir, WindDir9am,Sunshine, CloudTomorrow), 
                          hidden = 2)


# neuralnetwork = neuralnet(CloudTomorrow~ + Day + Month + MinTemp + MaxTemp
#                           + Evaporation + Sunshine + WindGustDir + WindGustSpeed
#                           + WindDir3pm + WindDir9am + WindSpeed3pm + WindSpeed9am + Humidity3pm
#                           + Humidity9am + Pressure3pm + Pressure9am + Temp3pm + Temp9am
#                           , data = annset.train %>% select(-Year, - Location, -Rainfall)
#                           , hidden = 3)


# plot
plot(neuralnetwork, rep = "best")
# neuralnetwork$result.matrix


# test
predneunet = compute(neuralnetwork, annset.test%>%select(Sunshine, WindDir3pm, WindGustDir, WindDir9am))
predneunet
neunetpred = as.data.frame(round(predneunet$net.result,0))
neunetpred

# conf mat
neutable = table(annset.test$CloudTomorrow, neunetpred$V1)
neutable


# finding accuracy and auc
accuneunet = round(
  (neutable[2,2]+neutable[1,1])/
    (neutable[2,2]+neutable[1,1]+neutable[2,1]+neutable[1,2]),
  2)

# aucneunetnum = round(performance(neuralnetwork, "auc"),2)



detach(package:neuralnet,unload = T)















