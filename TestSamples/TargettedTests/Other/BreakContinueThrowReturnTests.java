public class BreakContinueThrowReturnTests {

    public void doWhileWithBreak()
    {
        int x = 0;
        do{
            x += 2;
            break;
        }while(true);
    }

    public void whileWithContinue()
    {
        int x = 0;
        int count_evens = 0;
        while (x < 100)
        {
            x++;
            if (x % 2 == 1)
            {
                continue;
            }
            count_evens++;
        }
    }

    public int SwitchWithReturn() {
        int switchCondition = 0;
        switch (switchCondition) {
            case 1:
                return 2;
            case 3:
                boolean t = true;
            default:
                int x = 0;
        }
        return switchCondition;
    }

    public void IfWithThrow()
    {
        int x = 0;
        if(x == 1)
        {
            throw new RuntimeException();
        }
        x = 2;
    }

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
