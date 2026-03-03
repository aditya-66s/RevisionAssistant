package Models;
import java.util.ArrayList;

// Stack with a Type parameter, so I can use the same class for separate data types
public class Stack<T> {
    // Array List due to its mutability;
    private final ArrayList<T> stackData;
    // Pointer pointing to the last element added
    private int top;

    // Constructor for creating the stack
    public Stack(){
        stackData = new ArrayList<>();
        top = -1;
    }

    // Push Method to add an item at the top of the stack
    public void push(T item){
        stackData.add(item);
        top++;
    }

    // Pop method to remove and return the top item
    public T pop(){
        if(!isEmpty()){
            T item = stackData.get(top);
            top--;
            return item;
        }
        return null;
    }

    // Peek method allows user to see what item was last added
    public T peek(){
        if(!isEmpty()){
            return stackData.get(top);
        }
        return null;
    }

    // Function to see if the stack isEmpty
    public boolean isEmpty(){
        return top == -1 || stackData.isEmpty();
    }

    // Function returning the size of the stack
    public int size(){
        return stackData.size();
    }

    // Function to clear the stack
    public void clear(){
        top = -1;
        stackData.clear();
    }
}
