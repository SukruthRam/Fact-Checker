# SNLP Fact-Checker
Build a corpus-driven fact-checking engine, which returns a truth
value 0 (fact is false) or 1 (fact is true) given a fact from
DBpedia

# Approach
This fact-checker uses Wikipedia as the source for the data.
 
## Training
First, the input file containing the training data is read. The keyword from each 
fact in the file is recognised and stored. The facts in the training data file are divided into different categories based on these keywords. Each fact is parsed to get the respective subject, predicate and object. The subject is used to query Wikipedia. A map is created for each category, with the subject of each fact in the category as key and the content of the infobox of the Wikipedia results for the subject as value.
We check if the object of the fact is present in the content of the infobox. If it is present, the truth value is set to 1. If not, the truth value is set to 0.

## Fact-Checking
The input file containing the test data is read. For each fact in the file, the keyword is identified along with the subject, predicate and object.
 The map corresponding to the keyword is accessed. The map is searched with the subject as key. 
 If the object of the fact is present in the value of this key, the truth value is set to 1. 
 If it is not present, the truth value is set to 0. In cases where the subject itself is not present in the map as key, we again query Wikipedia to get the corresponding infobox. The object is searched in the obtained content. If present, truth value is set to 1 else it is set to 0.
 
# Execution
This project is to be compiled with Java version higher than or equal to Java11. It can be run by cloning the repository and running the main method from the IDE.


## False Positives

1.  ”New York City is IBM’s innovation place 0.0”. (This fact returns true because innovation place is replaced by string ’location’and location in wiki json response string contains New York.)
2.  ”Ben Gordon’s team is Charlotte Bobcats 0.0”. (This fact returns true because wiki json response string contains ’CharlotteBobcats’ in team for Ben Gordon.)
3.  ”Utah Jazz is Nazr Mohammed’s squad. 0.0”. (This fact returns true because wiki json response string contains ’Utah Jazz’in draft team for Nazr Mohammed.)
4.  ”Mike Dunleavy, Jr.’s team is Cleveland Cavaliers. 0.0”. (This fact returns true because wiki json response string contains ’ClevelandCavaliers’ in team for Mike Dunleavy Jr.)
5.  ”Nobel Prize in Physics is John Strutt, 3rd Baron Rayleigh’s squad. 0.0”. (”John Strutt, 3rd Baron Rayleigh’s squad” is awarded with nobel prize inphysics so it is returning true.)

## False Negatives

1.  ”A  Connecticut  Yankee  in  King  Arthur’s  Court’s  author  is  Mark  Twain.1.0”. (System  removes  all  ”’s”  form  fact  statement  so  the  book  ”A  ConnecticutYankee in King Arthur’s Court’s” will be converted to ”A Connecticut Yan-kee in King Arthur Court”, there is no wiki page for ”A Connecticut Yankeein King Arthur Court”.)
2.  ”Camp Rock stars Nick Jonas. 1.0”. (Nick Jonas is not mentioned in starring section of info box. But it is therein the wiki page of Camp Rock.)
3.  ”ABC Family Worldwide Inc.’s subsidiary is Television South. 1.0” (No subsidiary is mentioned in its wiki page.)
4.  ”The World Is Round’s author is Tony Rothman. 1.0”. (No proper wiki page for the book The World is Round.)
5.  ”Howard K. Stern’s spouse is Anna Nicole Smith. 1.0”. (No info box in Howard K. Stern’s wiki page.)

#### Team Name: IntelliSoft
#### Members:
1. Sukruth Ramesh
2. Rahul Sheriker
3. Catherine Tony 