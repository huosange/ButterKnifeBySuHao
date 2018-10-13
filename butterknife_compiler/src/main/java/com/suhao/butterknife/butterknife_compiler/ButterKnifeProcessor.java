package com.suhao.butterknife.butterknife_compiler;

import com.google.auto.service.AutoService;
import com.suhao.butterknife.butterknife_annotations.BindView;

import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils=env.getElementUtils();
        typeUtils=env.getTypeUtils();
        filer=env.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types=new LinkedHashSet<>();
        for(Class<? extends Annotation> annotation:getSupportedAnnotations()){
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations(){
        Set<Class<? extends Annotation>> annotations=new LinkedHashSet<>();

        annotations.add(BindView.class);

        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //取到所有含有BindView注解的java文件的信息
        Set<? extends Element> elementSet=roundEnvironment.getElementsAnnotatedWith(BindView.class);
        //key表示一个类，value表示这个类中所有的BindView
        Map<String,List<VariableElement>> cacheMap=new HashMap<>();
        for(Element element:elementSet){
            VariableElement variableElement= (VariableElement) element;
            String activityName=getActivityName(variableElement);
            List<VariableElement> list=cacheMap.get(activityName);
            if(list==null){
                list=new ArrayList<>();
                cacheMap.put(activityName,list);
            }
            list.add(variableElement);
        }

        Iterator iterator=cacheMap.keySet().iterator();
        //每一个key对应的就是一个java文件
        while(iterator.hasNext()){
            //准备生成java文件信息
            String activity= (String) iterator.next();
            //获取当前activity中所有被BindView注解的成员
            List<VariableElement> cacheElement=cacheMap.get(activity);
            //获取包名
            String packageName=getPackageName(cacheElement.get(0));
            //获取要生成的文件的文件名
            String newActivityBinder=activity+"$ViewBinder";

            Writer writer=null;
            try{
                JavaFileObject javaFileObject=filer.createSourceFile(newActivityBinder);
                writer=javaFileObject.openWriter();
                String activitySimple=cacheElement.get(0).getEnclosingElement().getSimpleName().toString()+"$ViewBinder";
                //写入文件中
                writer.write("package "+packageName+";");
                writer.write("\n");
                writer.write("import "+packageName+".ViewBinder;");
                writer.write("\n");
                writer.write("public class "+activitySimple+" implements ViewBinder<"+activity+">{");
                writer.write("\n");
                writer.write("public void bind("+activity+" target){");
                writer.write("\n");

                for(VariableElement variableElement:cacheElement){
                    BindView bindView=variableElement.getAnnotation(BindView.class);
                    //取得成员变量名
                    String fieldName=variableElement.getSimpleName().toString();
                    //取得成员变量名的类型
                    TypeMirror typeMirror=variableElement.asType();
                    writer.write("target."+fieldName+"=("+typeMirror.toString()+")target.findViewById("+bindView.value()+");");
                    writer.write("\n");
                }
                writer.write("}");
                writer.write("\n");
                writer.write("}");
                writer.write("\n");
                writer.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getActivityName(VariableElement variableElement){
        String packageName=getPackageName(variableElement);
        Element typeElement=variableElement.getEnclosingElement();
        return packageName+"."+typeElement.getSimpleName().toString();
    }

    private String getPackageName(VariableElement variableElement){
        Element typeElement=variableElement.getEnclosingElement();
        String packageName=elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        return packageName;
    }
}
