/*
 * AssosiativeDataSorter.h
 *
 *      Author: Owner
 */

#ifndef ASSOCIATIVEDATASORTER_H_
#define ASSOCIATIVEDATASORTER_H_

#include "AssociativeData.h"

class AssociativeDataSorter {


public:
    double d;
    AssociativeData data;

    AssociativeDataSorter();

    virtual ~AssociativeDataSorter();

};

#endif /* ASSOSIATIVEDATASORTER_H_ */


//package com.mlins.locator;
//
//public class AssociativeDataSorter {
//	private double d;
//
//	public AssociativeData data;
//
//
//	public AssociativeDataSorter(AssociativeData associativeData, double d2) {
//		this.data = associativeData;
//		this.setD(d2);
//	}
//	public int compare(AssociativeDataSorter other)
//	{
//
//		return Double.compare(getD(), other.getD());
//	}
//	public double getD() {
//		return d;
//	}
//	public void setD(double d) {
//		this.d = d;
//	}
//}
