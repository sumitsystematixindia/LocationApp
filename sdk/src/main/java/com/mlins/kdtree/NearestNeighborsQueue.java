package com.mlins.kdtree;


public class NearestNeighborsQueue {

    public static int REMOVE_HIGHEST = 1;
    public static int REMOVE_LOWEST = 2;

    NNPQueue m_Queue = null;
    int m_Capacity = 0;

    public NearestNeighborsQueue(int capacity) {
        m_Capacity = capacity;
        m_Queue = new NNPQueue(m_Capacity, Double.POSITIVE_INFINITY);
    }

    public double getMaxPriority() {
        if (m_Queue.length() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return m_Queue.getMaxPriority();
    }

    public boolean addElement(Object object, double priority) {
        if (m_Queue.length() < m_Capacity) {
            m_Queue.add(object, priority);
            return true;
        }
        if (priority > m_Queue.getMaxPriority()) {
            return false;
        }
        m_Queue.remove();
        m_Queue.add(object, priority);
        return true;
    }

    public boolean isFull() {
        return m_Queue.length() >= m_Capacity;
    }

    public Object getHighestElement() {
        return m_Queue.front();
    }

    public boolean isEmpty() {
        return m_Queue.length() == 0;
    }

    public int getSize() {
        return m_Queue.length();
    }

    public Object removeHighestElement() {
        return m_Queue.remove();
    }

    // modified Queue data structure
    private class NNPQueue {

        private double maxPriority = Double.MAX_VALUE;


        private Object[] data;


        private double[] value;


        private int count;


        private int capacity;


//	    public PQueue() {
//	        init(20);
//	    }
//
//	    public PQueue(int capacity) {
//	        init(capacity);
//	    }


        public NNPQueue(int capacity, double maxPriority) {
            this.maxPriority = maxPriority;
            init(capacity);
        }


        private void init(int size) {
            capacity = size;
            data = new Object[capacity + 1];
            value = new double[capacity + 1];
            value[0] = maxPriority;
            data[0] = null;
        }


        public void add(Object element, double priority) {
            if (count++ >= capacity) {
                expandCapacity();
            }
            /* put this as the last element */
            value[count] = priority;
            data[count] = element;
            heapifyUp(count);
        }


        public Object remove() {
            if (count == 0)
                return null;
            Object element = data[1];
	        /* swap the last element into the first */
            data[1] = data[count];
            value[1] = value[count];
	        /* let the GC clean up */
            data[count] = null;
            value[count] = 0L;
            count--;
            heapifyDown(1);
            return element;
        }

        public Object front() {
            return data[1];
        }

        public double getMaxPriority() {
            return value[1];
        }


        private void heapifyDown(int position) {
            Object element = data[position];
            double priority = value[position];
            int child;

            for (; position * 2 <= count; position = child) {
                child = position * 2;

                if (child != count)

                    if (value[child] < value[child + 1])
                        child++;

                if (priority < value[child]) {
                    value[position] = value[child];
                    data[position] = data[child];
                } else {
                    break;
                }
            }
            value[position] = priority;
            data[position] = element;
        }


        private void heapifyUp(int position) {
            Object element = data[position];
            double priority = value[position];
            while (value[position / 2] < priority) {
                value[position] = value[position / 2];
                data[position] = data[position / 2];
                position /= 2;
            }
            value[position] = priority;
            data[position] = element;
        }

        private void expandCapacity() {
            capacity = count * 2;
            Object[] elements = new Object[capacity + 1];
            double[] prioritys = new double[capacity + 1];
            System.arraycopy(data, 0, elements, 0, data.length);
            System.arraycopy(value, 0, prioritys, 0, data.length);
            data = elements;
            value = prioritys;
        }

//	    public void clear() {
//	        for (int i = 1; i < count; i++) {
//	            data[i] = null;
//	        }
//	        count = 0;
//	    }


        public int length() {
            return count;
        }

    }
}
