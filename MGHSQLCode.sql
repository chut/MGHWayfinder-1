CREATE TABLE tblNode (
	nID TEXT PRIMARY KEY NOT NULL UNIQUE, 
	x INT NOT NULL, 
	y INT NOT NULL, 
	nFloor INT NOT NULL, 
	nType TEXT NOT NULL, 
	nDep TEXT NOT NULL);
	
CREATE TABLE tblNeighbors (
	mNode TEXT NOT NULL, 
	nNode TEXT NOT NULL, 
	CONSTRAINT compKey PRIMARY KEY (mNode, nNode),
	CONSTRAINT mNodeFK FOREIGN KEY mNode3 REFERENCES tblNode(nID),
	CONSTRAINT nNodeFK FOREIGN KEY nNode REFERENCES tblNode(nID));
	
