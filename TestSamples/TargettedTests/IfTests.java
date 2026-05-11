    
class OuterClass {
	public void ForWithIfWithAll() {

        for (int x = 1; x < 2; x += 9) {
            if (x == 3) {
                x = 4;
                return;
            } else if (x == 5) {
                x = 6;
                break;
            } else if (x == 7) {
                x = 8;
                throw new IllegalArgumentException();
            } else {
                x = 9;
                continue;
            }
        }
    }
}