<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://infoml.org/infomlFile" xpath-default-namespace="http://infoml.org/infomlFile"
  version="2.0">

  <!-- 
Purpose: to convert version 0.87 infocards to version 0.90
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
  <xsl:template match="last">
    <xsl:element name="surname">
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- change 'key' element to 'tag' -->
  <xsl:template match="key">
    <xsl:element name="tag">
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- delete 'cardId' tag -->
  <xsl:template match="cardId"/>

  <!-- add cardId value as an attribute of infoml element -->
  <xsl:template match="infoml">
    <xsl:element name="infoml">
      <xsl:attribute name="cardId">
        <xsl:value-of select="cardId"/>
      </xsl:attribute>
      <xsl:copy-of select="@encoding"/>
      <xsl:attribute name="version">0.90</xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- move title element from properties to data element -->
  <xsl:template match="title"/>

  <xsl:template match="data">
    <xsl:element name="data">
      <xsl:if test="exists(../properties/title)">
        <xsl:text>
            
        </xsl:text>
        <xsl:element name="title">
          <xsl:value-of select="../properties/title/text()"/>
        </xsl:element>
      </xsl:if>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  
  <!-- change point attribute (of point element) to 'value' -->
  <xsl:template match="@point">
    <xsl:attribute name="value">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

  <!-- Copy all the attributes and other nodes -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
