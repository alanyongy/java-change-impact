class OuterClass {
    void method(){
		while(whileCondition1){
			int var2 = 2;
			int var3 = 3;
			{
				while(whilecond4){
					int var2 = 5;
					int var3 = 6;
				}
				while(whilecond7){
					{
						if(cond8) {
							break;
						}
					}
				}
			}	
		}
		
		int out = 9;
	}
}

//for all constructnodes
//if last node isConstruct
//	add each element inside last node's edgesAwaitingOutNode to parent's edgesAwaitingOutNOde
	
	
	
