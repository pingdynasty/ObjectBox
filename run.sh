DIR=`dirname $0`
CLASSPATH=$DIR/build/classes
for nm in $DIR/lib/*.jar $DIR/src/xslt $DIR/src/oml
 do CLASSPATH=$CLASSPATH:$nm
done
export CLASSPATH
test -z $JAVA_OPTIONS && JAVA_OPTIONS='-ea'
#EXTENSIONS="$EXTENSIONS -e org.oXML.extras.java.JavaExtensions"
#EXTENSIONS="$EXTENSIONS -e org.oXML.extras.db.DatabaseExtensions"
#EXTENSIONS="$EXTENSIONS -e org.oXML.extras.reflection.ReflectionExtensions"
#EXTENSIONS="$EXTENSIONS -e org.oXML.extras.xinclude.XIncludeExtensions"
java $JAVA_OPTIONS org.oXML.engine.ObjectBox $EXTENSIONS $@
