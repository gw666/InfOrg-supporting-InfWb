<?xml version="1.0" encoding="UTF-8"?>

<!-- 
    
    adding-ptrs-to-v1_00.xsl
    
    Version: 1.00
    
    Created: 7/21/2008
    
    Last modified: 11/7/2010
    
    Adds the beginning "ptrs" infocard to an otherwise valid infocard file
    (infomlFile, schema version 1.00). These pointers are needed
    for the resulting infocard file to be readable by Infocard Organizer.
    
    PURPOSE: This stylesheet is usually used when you have selected a set of infocards
    (either manually, or through the use of some XML tool), and you want to
    manipulate these infocards using Infocard Organizer.
    
    USAGE: Wrap the list of infoml elements with an infoml-file element, then
    run this stylesheet on the resulting XML file.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i="http://infoml.org/infomlFile"
    xmlns="http://infoml.org/infomlFile" version="2.0">
    
    <xsl:template match="*|@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="i:infomlFile">
        <xsl:copy>
            <infoml cardId="gw667_1" encoding="UTF-8" version="1.00" xsl:exclude-result-prefixes="i">
                <data>
                    <pointers>
                        <xsl:for-each select="/*/i:infoml">
                            <xsl:element name="ptr">
                                <xsl:attribute name="targetId">
                                    <xsl:value-of select="./@cardId"/>
                                </xsl:attribute>
                            </xsl:element>
                        </xsl:for-each>
                    </pointers>
                    <xsl:text>
                    </xsl:text>
                </data>
            </infoml>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
