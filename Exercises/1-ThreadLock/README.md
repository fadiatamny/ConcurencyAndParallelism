# BEHAVIOR
The issues is that we have two threads running simultaniously accessing same memeory.
both doing operations of inc and dec onto the same memory space without any synchronisation between the two causing alot of conflicts and ordering in which we get to inconsistent behavior as when T1 accesses the memory and decrements it might have a different value in its scope than the true value.
therefore, we have a situation of differnet values at access time.