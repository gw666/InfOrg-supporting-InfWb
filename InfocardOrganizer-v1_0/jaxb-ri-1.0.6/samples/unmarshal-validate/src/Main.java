/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
 
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;

// import java content classes generated by binding compiler
import primer.po.*;

/*
 * $Id: Main.java,v 1.1 2008/11/03 06:51:20 greggw Exp $
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
public class Main {
    
    // This sample application demonstrates how to enable validation during
    // the unmarshal operations. 
    
    public static void main( String[] args ) {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            JAXBContext jc = JAXBContext.newInstance( "primer.po" );
            
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            // enable validation
            u.setValidating( true );
            
            // in this example, we will allow the Unmarshaller's default
            // ValidationEventHandler to receive notification of warnings 
            // and errors which will be sent to System.out.  The default
            // ValidationEventHandler will cause the unmarshal operation
            // to fail with an UnmarshalException after encountering the
            // first error or fatal error.
            
            // unmarshal an invalid po instance document into a tree of Java 
            // content objects composed of classes from the primer.po package.
            System.out.println("NOTE: This sample is working correctly if you see validation errors!!");
            PurchaseOrder po = 
                (PurchaseOrder)u.unmarshal( new File( "po.xml" ) );
                
        } catch( UnmarshalException ue ) {
            // The JAXB specification does not mandate how the JAXB provider
            // must behave when attempting to unmarshal invalid XML data.  In
            // those cases, the JAXB provider is allowed to terminate the 
            // call to unmarshal with an UnmarshalException.
            System.out.println( "Caught UnmarshalException" );
        } catch( JAXBException je ) {
            je.printStackTrace();
        }
    }
}
