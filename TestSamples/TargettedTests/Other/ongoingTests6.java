class OuterClass {
    void method(){
		int var = 0.1;
		int var = 0.2;
		while(whileCondition1){
			int var = 2;
			int var = 3;
			{
				int x = 4;
				switch (conditionVar5) {
					case 6:
						x = 7;
						break;
					case 8:
						x = 9;
					default:
						x = 10;
						while(whilecond10){
							int x = 10.2;
							return;
						}
				}
				continue;
				while(whilecond11){
					{
						if(cond12) {
							throw nullPointerException;
						}
					}
				}
			}
		}
		
		while(condend){
			int  x = 2;
		}
	}
}

//for all constructnodes
//if last node isConstruct
//	add each element inside last node's edgesAwaitingOutNode to parent's edgesAwaitingOutNOde
	
	
	
