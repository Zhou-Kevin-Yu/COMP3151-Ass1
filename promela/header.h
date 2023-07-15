#define CAPACITY 10
#define READ_LOCK 2
#define WRITE_LOCK 1
#define UNLOCKED 0

inline signal(s)  {s++;}
inline wait(s)  {atomic{s > 0; s--;}}

inline unlock(s)  {
    if
    ::  s >= 0 ->
        locks[s] = UNLOCKED;
    fi;
}
inline write_lock(s)  {atomic{locks[s] == UNLOCKED; locks[s] = WRITE_LOCK}} //free to read
inline read_lock(s)  {atomic{locks[s] == UNLOCKED; locks[s] = READ_LOCK}} //cant read nor write
inline read(e)  {atomic{locks[e] <= WRITE_LOCK}}
inline init_locks() {
    byte i = 0;
    do
    ::  if
        ::  i == CAPACITY ->
            break;
        :: else ->
            locks[i] = UNLOCKED;
            i++;
        fi;
    od;
}

byte locks[CAPACITY];
byte capacity_s = CAPACITY;


inline lock_random_between_bound(L, R, M)   {
    byte i = L;
    do
    ::  if
        ::  i == R ->
            break;
        ::  else ->
            i++;
        fi;
    ::  write_lock(M)
        M = i;
        break;
    od;
}