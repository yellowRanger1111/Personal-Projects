library(ggplot2)
library(dplyr)
library(lattice)
library(reshape2)
library(GGally)
library(igraph)
library(igraphdata)


options(repr.plot.width=6, repr.plot.height=4)



rm(list = ls())
set.seed(30039096) 
webforum = read.csv("webforum.csv")
webforum = webforum [sample(nrow(webforum), 20000), ]

summary(webforum)

#tidy data
webcopy = webforum %>%
  mutate(Date = as.Date(Date)) %>%
  mutate(Time = as.POSIXlt(Time, format = '%M:%S')) %>%
  mutate(year = as.numeric(format(Date, "%Y"))) %>%
  mutate(month = as.numeric(format(Date, "%m"))) %>%
  mutate(second = as.numeric(format(Time, "%M")) * 60  + as.numeric(format(Time, "%S"))) %>%
  mutate(yearmonth = format(Date,"%Y-%m")) %>%
  filter(AuthorID != -1)
  # mutate(Time = as.POSIXlt(Time, format = '%H:%M')) %>%
  # mutate(Time = hour(Time) * 60 + minute(Time, format = '%H:%M')))



##### QUESTION 1 #####

# webcopy <- webforum[1:5000,] # one to 5000 row, all collumn


# one = finding freq of post on year and month
one = as.data.frame(as.table(by(webcopy$ThreadID, list(webcopy$year, webcopy$month), length)))
colnames(one) = c("Year", "Month", "Freq")
arrange(one, Year)
# one <- one %>%
#   group_by(year)

# one = one %>%
#   mutate(month_year = as.yearmon(paste(Year, Month), "%Y %m"))

#one$Date = as.yearmon(paste(one$Year, one$Month), "%Y %m")

# summary(one)

ggplot(data = one, aes(x = Month, y = Freq, color = Year)) + 
  geom_point() + 
  facet_wrap(~Year, nrow = 4) +
  scale_y_continuous(name ="No of Post")


ggplot(data = one, aes(x = Month , y = Freq, color = Year, group = Year)) + 
  geom_line()

#-----------------------------------#
# two = finding freq of post on year only
two = as.data.frame(as.table(by(webcopy$ThreadID, list(webcopy$year), length)))
colnames(two) = c("Year", "Freq")
two[is.na(two)] = 0

ggplot(data = two, aes(x = Year, y = Freq, group = 1)) + 
  geom_line() + 
  geom_point() + 
  geom_text(aes(label = Freq), vjust = -1, nudge_y = -2, size = 5) +
  theme(panel.background = element_rect(fill = "white",colour = "white",size = 0.1, linetype = "solid"))

ggplot(data = two, aes(x = Year, y = Freq, group = 1)) + 
  geom_bar(stat = "identity", fill = "lightblue", color = "black") +
  geom_text(aes(label = Freq), nudge_y = 90, size = 5, color = "navy") +
  theme(panel.background = element_rect(fill = "white",colour = "white",size = 0.1, linetype = "solid")) +
  scale_y_continuous(name ="No of Post")
  



# PART B
my_data = aggregate(webcopy, by = list(webcopy$yearmonth), FUN = mean)

#-----------------------------------#
#finding authenticity and etc
three = my_data %>%
  select(Date = Group.1, year, month, Analytic, Clout, Authentic, Tone)
three = melt(three, id = c("Date", "year", "month"))
colnames(three) = c("Date","year", "month", "Linguistic", "Val")
ggplot(data = three, aes(x = Date, y = Val, group = Linguistic, color = Linguistic, las = 1)) + 
  geom_line() + 
  theme(axis.text.x = element_text(angle=90, size = 8), 
        panel.background = element_rect(fill = "white",colour = "white",size = 0.1, linetype = "solid")) +
  scale_y_continuous(name ="Average Value")

#-----------------------------------#
# emotions through post
four = my_data %>% 
  select(Date = Group.1, year, month, affect, posemo, negemo, anx)
four = melt(four, id = c("Date", "year", "month"))
colnames(four) = c("Date", "year", "month", "Emotion", "val")
ggplot(data = four, aes(x = month, y = val, group = Emotion, color = Emotion )) + 
  geom_line() + 
  theme(axis.text.x = element_text(angle=90, size = 9)) + facet_wrap(~year, nrow = 2) +
  scale_y_continuous(name ="Average Value")

#-----------------------------------#
# finding what point of view they are using
six = my_data %>% 
  select(Date = Group.1, year, month, i, we, you,they)

summary(six)
six = melt(six, id = c("Date", "year", "month"))
colnames(six) = c("Date","year", "month", "pov", "Val")
ggplot(data = six, aes(x = month, y = Val, group = pov, color = pov, las = 1)) + 
  geom_line() + 
  theme(axis.text.x = element_text(angle=90, size = 8)) + facet_wrap(~year, nrow = 2)  +
  scale_y_continuous(name ="Average Value")

#for report
new_six = six %>%
  filter(year < 2005)
summary(new_six)

new_six = six %>%
  filter(year >= 2005 & year <= 2008)
summary(new_six)

new_six = six %>%
  filter(year >2008)
summary(new_six)



#finding correlation
correl = round(cor(my_data[6:19]), digits = 3)
ggcorr(correl)





##### QUESTION 2 #####
seven = as.data.frame(as.table(by(webcopy$ThreadID, list(webcopy$ThreadID), length)))
colnames(seven) = c("ThreadID", "Post_Freq")
seven = arrange(seven, desc(Post_Freq))


# taking top 15 threads 
seven = head(seven, 15)

ggplot(data = seven, aes(x = ThreadID, y = Post_Freq, fill = ThreadID)) + 
  geom_bar(stat = "identity", color = "black",size=1) + 
  theme(panel.background = element_rect(fill = "white",colour = "white",size = 0.1, linetype = "solid")) + 
  geom_text(aes(label = Post_Freq), nudge_y = -3) +
  scale_y_continuous(name ="No of Post")


# top 5 threads
#252620
#283958
#127115
#145223
#472752

abcd = webcopy %>%
  filter(ThreadID == 252620 | ThreadID == 283958 | ThreadID == 127115 | ThreadID == 145223 | ThreadID == 472752) %>%
  group_by(ThreadID) %>%
  summarise(across(4:18, mean))

#-----------------------------------#
eight = abcd %>% 
  select(ThreadID, Analytic, Authentic, Clout, Tone, WC, WPS)

eight = melt(eight, id = c("ThreadID"))
colnames(eight) = c("ThreadID", "Var", "Val")
eight$Val = round(eight$Val, digits = 2)

eight$ThreadID = as.character(eight$ThreadID)

ggplot(data = eight, aes(x = ThreadID, y = Val, fill = Val)) +
  geom_bar(stat= "identity", color = "black", size = 1) +
  geom_text(aes(label = Val), color = "white", nudge_y = - 8)+
  facet_wrap(~Var, ncol = 2) +
  theme(panel.background = element_rect(fill = "white",colour = "white",size = 0.1, linetype = "solid")) +
  scale_y_continuous(name ="Average Value")

#-----------------------------------#
nine = abcd %>%
  select(ThreadID, i, we, you, they, affect, posemo, negemo, anx)

nine = melt(nine, id = c("ThreadID"))
colnames(nine) = c("ThreadID", "Var", "Val")
nine$Val = round(nine$Val, digits = 2)

nine$ThreadID = as.character(nine$ThreadID)

ggplot(data = nine, aes(x = ThreadID, y = Val, fill = Val)) +
  geom_bar(stat= "identity", color = "black", size = 1) +
  geom_text(aes(label = Val), nudge_y = 1)+
  facet_wrap(~Var, ncol = 2) +
  theme(panel.background = element_rect(fill = "white",colour = "white",size = 0.1, linetype = "solid")) +
  scale_y_continuous(name ="Average Value")


#-----------------------------------#
efgh = webcopy %>%
  filter(ThreadID == 252620 | ThreadID == 283958 | ThreadID == 127115 | ThreadID == 145223 | ThreadID == 472752) %>%
  group_by(ThreadID, year) %>%
  summarise(across(4:18, mean))

efgh$ThreadID = as.character(efgh$ThreadID)

#-----------------------------------#
# type of sentence
ten = efgh %>% 
  select(ThreadID, year, Analytic, Clout, Authentic, Tone)
ten = melt(ten, id = c("ThreadID", "year"))

ggplot(data = ten, aes (y = value, x = year, color = ThreadID)) + 
  geom_line(size = 1) +
  geom_point() +
  facet_wrap(~variable, nrow =2) +
  scale_y_continuous(name ="Average Value") + 
  scale_x_continuous(name = "Year")

#-----------------------------------#
# pov of conv over time
eleven = efgh %>%
  select(ThreadID, year, i, we, you, they, affect, posemo, negemo, anx)
eleven = melt(eleven, id = c("ThreadID", "year"))

ggplot(data = eleven, aes (y = value, x = year, color = ThreadID)) + 
  geom_line(size = 1) +
  geom_point() +
  facet_wrap(~variable, nrow =3) +
  scale_y_continuous(name ="Average Value") + 
  scale_x_continuous(name = "Year")







######   QUESTION 3    ########

authors =as.data.frame(
  as.table(by(webcopy,list(webcopy$yearmonth,webcopy$ThreadID),function(df)
    length(unique(df$AuthorID)))))
colnames(authors) = c("Year","ThreadID","Auuthors")
authors[is.na(authors)] = 0
authors = arrange(authors, desc(Auuthors))

#getting most post
top_authors = head(authors, 5)
top1 = head(authors,1)

#finding post
nanana = as.data.frame(as.table(by(webcopy$ThreadID, list(webcopy$yearmonth, webcopy$ThreadID), length)))
nanana[is.na(nanana)] = 0
nanana = nanana %>% 
  filter(Freq > 8)
show(nanana)


nanana %>%
  filter(ThreadID == 513737)



#for thread 1
twelve = webcopy %>%
  filter(yearmonth == "2010-12" | yearmonth == "2011-01" )
twelve = arrange(twelve, ThreadID)

# i choose thread 767538 and 763776 for date = 2010-12 - 2011 - 01

first_t = twelve %>%
  filter(ThreadID == 767538 & yearmonth == "2010-12") %>%
  select(AuthorID)
first_t = arrange(distinct(first_t, AuthorID), AuthorID)

second_t = twelve %>%
  filter(ThreadID == 763776 & yearmonth == "2010-12") %>%
  select(AuthorID)
second_t = arrange (distinct(second_t, AuthorID), AuthorID)


first_t = twelve %>%
  filter(ThreadID == 767538 & yearmonth == "2011-01") %>%
  select(AuthorID)
first_t = arrange (distinct(first_t, AuthorID), AuthorID)

second_t = twelve %>%
  filter(ThreadID == 763776 & yearmonth == "2011-01") %>%
  select(AuthorID)
second_t = arrange (distinct(second_t, AuthorID), AuthorID)



# for 2010 -12
abc = graph_from_literal(64190:103846:128573:149762:220509:221619 -- 64190:103846:128573:149762:220509:221619)
def = graph_from_literal(143126:149762:181593:182813:198663:222733 -- 143126:149762:181593:182813:198663:222733)
gg = abc %u% def
plot(gg)

#for 2011 - 01 763776 - doesnt talk
ff = graph_from_literal(101907:143126:144737:169189:172022:182813:223889:226062 -- 101907:143126:144737:169189:172022:182813:223889:226062)
plot(ff)

evec = evcent(gg)
data_for_network = t(as.data.frame(rbind(as.table(degree(gg)),
                                       as.table(betweenness(gg)),
                                       as.table(closeness(gg)),
                                       as.table(evec$vector))))
colnames(data_for_network) = c("Degree", "Betweenness", "Closeness", "Eigenvector")
data_for_network = round(data_for_network, digits = 2)







