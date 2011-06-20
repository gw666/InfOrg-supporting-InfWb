<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://infoml.org/infomlFile" xmlns:i="http://infoml.org/infomlFile" version="2.0">

<!-- 
Purpose: to convert version 0.88 or 0.89 infocards to version 0.90
-->
  
  <!-- change version to '0.90' -->
  <xsl:template match="@version">
    <xsl:attribute name="version">0.90</xsl:attribute>
  </xsl:template>

  <!-- change 'last' element to 'surname' -->
  <!-- 
    IMPORTANT Both 'xmlns:' and 'xmlns:i' *must* be present (above)
    and that, in the template below, the match string must include
    the namespace prefix 'i:' and the element created must *not*
  -->
  <xsl:template match="i:last">
    <xsl:element name="surname">
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- Copy all the attributes and other nodes -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
