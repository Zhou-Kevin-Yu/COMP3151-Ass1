#include "operations.h"

#define CAPACITY 10

#define semaphore byte

inline signal(s) {s++;}
inline wait(s) {atomic{s > 0; s--}}


byte data[CAPACITY];
semaphore capacity = CAPACITY;

proctype insert(int x) {
    do
    ::  wait(capacity);
        byte i = 0;
        byte L = -1;
        byte R = -1;
        do
        ::  wait(shift_locks[i]);
            signal(shift_locks[i - 1]) //if not first element
            if
            ::  data[i] == x ->
                signal(capacity);
                signal(shift_locks[i])
                break;
            ::  data[i] < x ->
                signal(shift_locks[L])
                L = i;
            ::  data[i] > x ->
                if
                ::  L > -1 ->
                    shift_left(i - 1);
                    //Atomic recursive function that calls shift_left on the left element, waits on current element
                    //Base case: returns true if called on empty element, false if called on the leftmost element - should never return false
                    //recursive case: if recursive shift_left call returns true, set left element to current element, then empty current element
                    //blocks if element is locked
                    data[i - 1] = x;
                    signal(shift_locks[L]);
                    signal(shift_locks[i]);
                    break;
                    :: else ->
                    R = i;
                    i++;
                    do
                    ::  wait(shift_locks[i]);
                        if
                        ::  data[i] > -1 ->
                            wait(shift_locks[i + 1]);
                            signal(shift_locks[i]);
                            i++;
                        ::  else ->
                            wait(shift_locks[i + 1])
                            signal(shift_locks[i])
                            shift_right(R)
                        fi;
                    od;
                fi;
            fi;
        od;
    od;
};



proctype delete(int x) {
    do:
    ::  data[search(x)] = -1;
    od;
};


proctype search(int x) {
    L = 0;
    R = CAPACITY - 1;
    M = L + R / 2
    do:
    wait(shift_locks[M])
    ::  if
        ::  data[M] < x ->
            temp = L;
            L = M;
            M = L + R / 2
            wait(shift_locks[M]);
            signal(shift_locks[temp]); //if not leftmost
        ::  data[M] > x ->
            R = M;
            temp = R;
            R = M;
            wait(shift_locks[M])
            signal(shift_locks[temp])
        ::  data[M] == x ->
            signal(shift_locks[M])
        



};

proctype member() {

};

proctype print_sorted() {
    do:
    ::  i = 0;
        do:
        ::  if 
            ::  i < CAPACITY ->
                wait(shift_locks[i]);
                print(data[i]);
                wait(shift_locks[i + 1]);
                signal(shift_locks[i]);
                i++;
            ::  else ->
                signal(shift_locks[i])
                break;
            fi;
        od;
    od;
};

init {
    byte i = 0;
    do
    ::  semaphore shift_locks[CAPACITY];
        shift_locks[i] = 1;
        i++;
        if
        ::  i == CAPACITY ->
            break;
        :: else ->
            skip;
        fi;
    od
    run insert(); run delete(); run member(); run print_sorted();
    run insert(); run delete(); run member(); run print_sorted();
}