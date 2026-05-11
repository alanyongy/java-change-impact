public class ShellSort {
	//Iterative Shell Sort using novel gap sequence
	public static void shellSortIterative(int[] array) {
		int n = array.length;
		
		int gap = 1;
		while(gap < n) {
			gap = gap * 3;
		}
		shellSortIterativeHelper(array, n, gap/3);
	}
	
	public static void shellSortIterativeHelper(int[] array, int n, int gap){
		while(gap > 0) {
			for(int i = gap; i < n; i++) {
				int temp = array[i];
				int j = i;
				while(j >= gap && array[j-gap] > temp) {
					array[j] = array[j-gap];
					j = j - gap;
				}
				array[j] = temp;
			}
			gap = gap/3;
		}
	}
	
	//Recursive Shell Sort using novel gap sequence
	public static void shellSortRecursive(int[] array) {
		int n = array.length;
		int gap = 1;
		while(gap < n) {
			gap = gap * 3;
		}
		shellSortRecursiveHelper(array, n, gap/3);
	}
	
	public static void shellSortRecursiveHelper(int[] array, int n, int gap) {
		if(gap > 0) {
			for(int i = gap; i < n; i++) {
				int temp = array[i];
				int j = i;
				while(j >= gap && array[j-gap] > temp) {
					array[j] = array[j-gap];
					j = j - gap;
				}
				array[j] = temp;
			}
			shellSortRecursiveHelper(array, n, gap/3);
		}
	}

	public static void main(String[] args) {
		//Input array to iterative shell sort 
		int [] arrayIterative = {9, 8, 3, 7, 5, 6, 4, 1}; 
				
		//Input array to the recursive shell sort
		int [] arrayRecursive = {9, 8, 3, 7, 5, 6, 4, 1}; 
		
		//Iterative Shell Sort 
		shellSortIterative(arrayIterative); 
		System.out.println ("Array sorted using the iterative function:"); 
		printArray(arrayIterative);
		
		//Recursive Shell Sort 
		shellSortRecursive(arrayRecursive); 
		System.out.println("Array sorted using the recursive function:");
		printArray(arrayRecursive); 
		} 
		
		//Utility method to print an array 
		public static void printArray(int[] array) {
			for (int i = 0; i < array.size(); i++) { 
				System.out.print(array[i] + " "); 
			} 
			System.out.println(); 
		}
}