public class NestedConstructTests {
    void NestedConstructTest1(){
        int var = 0;
        while(var == 1){
            var = 2;
            var = 3;
            {
                int x = 4;
                switch (5) {
                    case 6:
                        x = 7;
                        break;
                    case 8:
                        x = 9;
                    default:
                        x = 10;
                        while(x == 10){
                            x = 11;
                            return;
                        }
                }
                while(var == 12){
                    {
                        if(var == 13) {
                            throw new NullPointerException();
                        }
                    }
                }
            }
        }
        int out = 14;
    }

    void NestedConstructTest2()
    {
        {
			for (int x = 0; x < 1; x++) {
				do {
					if (x == 2){
						{
							x = 3;
						}
					}
					else
					{
						int var = x==5 ? 6: 7;
					}
				}while(x == 8);
			}
			int x = 9;
        }
        int x = 10;
    }
}
