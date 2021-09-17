cd build/libs
nohup java -jar finopolis-1.0.0.jar &>/dev/null &
echo $! > ../../pid
