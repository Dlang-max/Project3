class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	public T[] _storage; //Ordered array with highest priority at the end.
	private int _numElements;

	@SuppressWarnings("unchecked") //This removes the errors for unchecked casting.
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	@SuppressWarnings("unchecked")
	public void add (T data) {
		if(_numElements == _storage.length){
			increaseStorage();
		}
		_storage[_numElements] = data;
		_numElements++;
		bubbleUp(_numElements - 1);
	}

	public T removeFirst () {
		T first = _storage[0];
		T last = _storage[_numElements - 1];
		_storage[_numElements - 1] = null;
		_storage[0] = last;
		_numElements--;
		trickleDown(0);
		return first;		
	}

	public int size () {
		return _numElements;
	}

	private void bubbleUp (int startingElementIndex){
		int i = startingElementIndex;
		T parent = _storage[getParentIndex(i)];
		while (_storage[i].compareTo(parent) > 0){
			//Swap nodes:
			_storage[getParentIndex(i)] = _storage[i];
			_storage[i] = parent;

			i = getParentIndex(i);
			parent = _storage[getParentIndex(i)];
		}
	}

	private void trickleDown(int startingElementIndex){
		int i = startingElementIndex;
		int largestChildIndex = getLargestChildIndex(i);
		T largestChild = _storage[largestChildIndex];

		while(_storage[i].compareTo(largestChild) < 0){
			//Swap nodes:
			_storage[largestChildIndex] = _storage[i];
			_storage[i] = largestChild;

			i = largestChildIndex;
			largestChildIndex = getLargestChildIndex(i);
			largestChild = _storage[largestChildIndex];
		}
			
		
	}

	private int getParentIndex(int index){
		return (index-1)/2;
	}

	public int getLeftChild(int index){
		int i = index * 2 + 1;
		if (i < _storage.length && _storage[i] != null){
			return i;
		}
		return index; //Returning same index makes it so compareTo() returns 0, so the trickleDown loop stops.
	}

	private int getRightChild(int index){
		int i = index * 2 + 2;
		if (i < _storage.length && _storage[i] != null){
			return i;
		}
		return index; //Returning same index makes it so compareTo() returns 0, so the trickleDown loop stops.
	}

	private int getLargestChildIndex(int index){
		int left = getLeftChild(index);
		int right = getRightChild(index);
		
		// if(_storage[left] == null && _storage[right] == null){
		// 	System.out.println("ERROR:");
		// 	System.out.println("I: " + index);
		// 	System.out.println("L: " + left);
		// 	System.out.println("R: " + right);
		// }
		if(_storage[left].compareTo(_storage[right]) > 0){
			return left;  
		}
		return right;
	}

	@SuppressWarnings("unchecked")
	private  void increaseStorage(){
		T[] array = (T[]) new Comparable[_numElements + INITIAL_CAPACITY];
		for(int i = 0; i < _numElements; i++){
			array[i] = _storage[i];
		}
		_storage = array;
	}
}

