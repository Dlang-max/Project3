
/**
 * A Heap is a data structure that maintains the Heap property. The Heap property is that the parent of any node has a higher priority than its children.
 * This implementation uses an array to store the data. The array is ordered so that the highest priority element is at the front of the array.
 */
class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	public T[] _storage; //Ordered array with highest priority at the end.
	private int _numElements;

	@SuppressWarnings("unchecked") //This removes the errors for unchecked casting.
	/**
	 * Creates a new HeapImpl.
	 */
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	
	@SuppressWarnings("unchecked")
	/**
	 * Builds a heap from the given array of data. Maintains the Heap propertyo
	 * by calling bubbleUp() when appropriate.
	 * 
	 * @param data the data being added to the heap
	 */
	public void add (T data) {
		if(_numElements == _storage.length){
			increaseStorage();
		}
		_storage[_numElements] = data;
		_numElements++;
		bubbleUp(_numElements - 1);
	}

	/**
	 * Returns the first element in the heap while also rremoving it from the heap.
	 * Maintains the Heap property by calling trickleDown() when appropriate.
	 * 
	 * @return the element with the highest priority
	 */
	public T removeFirst () {
		T first = _storage[0];
		T last = _storage[_numElements - 1];
		_storage[_numElements - 1] = null;
		_storage[0] = last;
		_numElements--;
		trickleDown(0);
		return first;		
	}

	/**
	 * Returns the number of elements in the heap.
	 * 
	 * @return the number of elements in the heap
	 */
	public int size () {
		return _numElements;
	}

	/**
	 * Maintains the heap property by swapping the element at index i with its parent
	 * 
	 * @param startingElementIndex the index being bubbled up
	 */
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

	/**
	 * Maintains the heap property by swapping the element at index i with its largest child
	 * 
	 * @param startingElementIndex the index being bubbled down
	 */
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

	/**
	 * Returns the parent index of the element at index index.
	 * 
	 * @param index the index of the child element
	 * @return the index of the parent element
	 */
	private int getParentIndex(int index){
		return (index-1)/2;
	}

	/**
	 * Calculates the index of the left child of element at index.
	 * 
	 * @param index The index of the element whose child to search.
	 * @return the index of the left child.
	 */
	public int getLeftChild(int index){
		int i = index * 2 + 1;
		if (i < _storage.length && _storage[i] != null){
			return i;
		}
		return index; //Returning same index makes it so compareTo() returns 0, so the trickleDown loop stops.
	}

	/**
	 * Calculates the index of the right child of element at index.
	 * 
	 * @param index The index of the element whose child to search.
	 * @return the index of the right child.
	 */
	private int getRightChild(int index){
		int i = index * 2 + 2;
		if (i < _storage.length && _storage[i] != null){
			return i;
		}
		return index; //Returning same index makes it so compareTo() returns 0, so the trickleDown loop stops.
	}

	/**
	 * Calculates the index of the largest child of the element in index index.
	 * 
	 * @param index: the index of the element whose children is being searcherd.
	 * @return the index of the largest child.
	 */
	private int getLargestChildIndex(int index){
		int left = getLeftChild(index);
		int right = getRightChild(index);
		
		if(_storage[left].compareTo(_storage[right]) > 0){
			return left;  
		}
		return right;
	}

	
	@SuppressWarnings("unchecked")
	/**
	 * Creates a new array that has INITIAL_CAPACITY more elements than the current _storage and copies over all the values. 
	 */
	private  void increaseStorage(){
		T[] array = (T[]) new Comparable[_numElements + INITIAL_CAPACITY];
		for(int i = 0; i < _numElements; i++){
			array[i] = _storage[i];
		}
		_storage = array;
	}
}

