package org.aki.helvetti.worldgen.biomesources;

public class CDeferredFinalHolder<T> {
    
    private T value;
    private volatile boolean hasValue = false;

    public synchronized void set(T value) {
        if (!this.hasValue) {
            this.value = value;
            this.hasValue = true;
        } else if (this.value != value) {
            throw new IllegalStateException("Attempting to change a CDeferredFinalHolder variable");
        }
    }

    public T get() {
        if (this.hasValue) {
            return this.value;
        } else {
            throw new IllegalStateException("Attempting to access a CDeferredFinalHolder value before it's initialized");
        }
    }

    public boolean hasValue() {
        return this.hasValue;
    }

}
