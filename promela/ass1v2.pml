#define CAPCITY 10
#define READ_LOCK 1
#define WRITE_LOCK 2
#define UNLOCKED 0

byte locks[CAPACITY];
byte data[10];

byte capacity_sem = CAPACITY;

inline signal(s) = {s++;}
inline wait(s) = {atomic{s > 0; s++}}
inline unlock(s) = {locks[s] = UNLOCKED}
inline write_lock(s) = {atomic{locks[s] == UNLOCKED; locks[s] = WRITE_LOCK}}
unline read_lock(s) = {atomic{locks[s] == UNLOCKED; locks[s] = READ_LOCK}}


proctype insert(int x) {
    do
    ::  wait(capacity);
        byte i = 0;
        byte L = -1;
        byte R = -1;
        do
        ::  write_lock(i);
            unlock(i - 1); //if not leftmost
            if
            ::  data[i] == x ->
                signal(capacity);
                unlock(i);
                break;
            ::  data[i] < 0 ->
                unlock(L);
                L = i;
            ::  data[i] > x ->
                if
                ::  L > -1 ->
                    write(L, i); //shift and insert
                    unlock(L);
                    unlock(i);
                    break;
                :: else ->
                    R = i; 
                    i++;
                    do
                    ::  write_lock(i)
                        if
                        ::  data[i] > -1 ->
                            unlock(i - 1); //if i - 1 not R
                            i++;
                        ::  else ->
                            unlock(i - 1) //if i - 1 not R
                            write(R, i);
                            unlock(R);
                            unlock(i);
                        fi;
                    od;
                fi;
            fi;
        od;
    od;
};

proctype search(int x) {
    L = 0;
    R = CAPACITY - 1;
    M = L + R / 2
    do
    write_lock(M)
    ::  if
        ::  data[M] < x ->
            temp = L;
            L = M;
            M = L + R / 2
            write_lock(M)
            unlock(temp);
        ::  data[M] > x ->
            R = M;
            temp = R;
            R = M;
            write_lock(M)
            unlock(temp);
        ::  data[M] == x ->
            break;
        fi;
    od;
}

proctype member(int x) {
    do
    ::  i = search(x);
        if
        ::  data[i] == x ->
            printf("%d exists", x);
            unlock(i)
        :: data[i] != x ->
            printf("%d does not exist", x);
            unlock(i)
        fi;
    od;
};

proctype delete(int x) {
    do
    ::  
        i = search(i);
        delete_cs(i);
    od;
}

proctype print_sorted() {
    do
    ::  i = 0;
        do
        ::  if 
            ::  i < CAPACITY ->
                locks[i] <= READ_LOCK;
                print(data[i]);
                i++;
            ::  else ->
                break;
            fi;
        od;
    od;
}

init {
    byte i = 0;
    do
    ::  shift_locks[i] = 1;
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

