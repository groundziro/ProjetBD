SQLiteDFManager
---
1.  _**Authors**_
**DACHY Corentin
JOSSE Thomas**
2.  _**Description**_
Here's our database project corresponding to the specifications that we were given.
It composes itself of a Graphical Interface and were written in the Java language.
3.  _**Instructions**_
To launch it all you need to do is type "ant" in the terminal where the current directory would be the directory where lies this README.
Then you click on the Browse button which will let you choose the DB you want to work with.
If your DB have conflicts (Table doesn't exist, Attributes of a FD doesn't exist in the table, redundance in the table with the corresponding FD or logical consequences),
you'll face a window where you'll resolve these conflicts by clicking on the different buttons.
Else you'll arrive at a window with the tables's name, keys and Functional Dependencies, and also buttons.
4.  _**ADD DF**_
You'll have to fill the textBoxes the corresponding elements, according to the prompt texts : "Table","LeftHandSide","RightHandSide".
If the table doesn't exist an alert appears and you have to change the Table textBox.
5.  _**EXIT**_
It brings you back to the first screen with the browse button.
6.  _**CHECK**_
After clicking it, click the button that represents the table you wanna check.
It will check if the table is BCNF or 3NF. If it isn't 3NF, you'll have the choice to decompose it in 3NF tables.
7.  _**MODIFY**_
After clicking it, fill the text boxes on the bottom of the window BEFORE clicking on the FD you want to modify.
8.  _**DELETE**_
After clicking it, click on the FD you wanna remove. It will ask you if wanna continue, click on OK to Delete another FD that you choose with the same method.
