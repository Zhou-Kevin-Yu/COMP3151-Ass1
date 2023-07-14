#include "critical2.h"

#define CAPACITY 10
#define READ_LOCK 1
#define WRITE_LOCK 2
#define UNLOCKED 0

inline signal(s)  {s++;}
inline wait(s)  {atomic{s > 0; s++}}

inline unlock(s)  {locks[s] = UNLOCKED}
inline write_lock(s)  {atomic{locks[s] == UNLOCKED; locks[s] = WRITE_LOCK}} //free to read
inline read_lock(s)  {atomic{locks[s] == UNLOCKED; locks[s] = READ_LOCK}} //cant read nor write

byte locks[CAPACITY];
byte data[CAPACITY];
byte capacity_s = CAPACITY;
byte insert_mutex = 1;

proctype insert(int x) {
    do
    ::  skip;
    ::  wait(insert_mutex);
        wait(capacity_s);
        byte i = 0;
        byte L = -1;
        byte R = -1;
        do
        ::  write_lock(i);
            unlock(i - 1); //if not leftmosts
            if
            ::  data[i] == x ->
                signal(capacity_s);
                unlock(i);
                break;
            ::  data[i] < 0 ->
                unlock(L);
                L = i;
            ::  data[i] > x ->
                if
                ::  L > -1 ->
                    critical_section(L, i);
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
                            critical_section(i, R);
                            unlock(R);
                            unlock(i);
                        fi;
                    od;
                fi;
            fi;
        od;
        signal(insert_mutex);
    od;
};

proctype member(int x) {
    byte L = 0;
    byte R = CAPACITY - 1;
    byte M = L + R / 2
    byte temp;
    do
    ::  write_lock(M);
        if
        ::  data[M] < x ->
            temp = L;
            L = M;
            M = L + R / 2;
            write_lock(M);
            unlock(temp);
        ::  data[M] > x ->
            temp = R;
            R = M;
            M = L + R / 2;
            write_lock(M)
            unlock(temp);
        ::  data[M] == x ->
            printf("%d does exist\n", x);
            break;
        ::  data[M] == L && data[M] == R && data[M] != x -> //all values locked
            printf("%d does not exist\n", x);
            break;
        fi;
        unlock(M);
        unlock(L);
        unlock(R);
    od;
}

proctype delete(int x) {
    byte L = 0;
    byte R = CAPACITY - 1;
    byte M = L + R / 2
    byte temp;
    do
    ::  write_lock(M);
        if
        ::  data[M] < x ->
            temp = L;
            L = M;
            M = L + R / 2;
            write_lock(M);
            unlock(temp);
        ::  data[M] > x ->
            temp = R;
            R = M;
            M = L + R / 2;
            write_lock(M)
            unlock(temp);
        ::  data[M] == x ->
            //delete value
            break;
        ::  data[M] == L && data[M] == R && data[M] != x -> //all values locked
            //value doesnt exist
            break;
        fi;
        unlock(M);
        unlock(L);
        unlock(R);
    od;
}

proctype print_sorted(int x) {
    byte i = 0;
    do
    ::
        do
        ::  if 
            ::  i < CAPACITY ->
                locks[i] <= READ_LOCK;
                printf("%d", data[i]);
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
    ::  locks[i] = UNLOCKED;
        i++;
        if
        ::  i == CAPACITY ->
            break;
        :: else ->
            skip;
        fi;
    od;
    i = 0;
    do 
    ::  if
        ::  i != CAPACITY ->
            run insert(i); run delete(i); run member(i); run print_sorted(i);
            run insert(i); run delete(i); run member(i); run print_sorted(i);
            i++;
        ::  else ->
            break;
        fi;
    od;
}


//no two inserts,
//no two
