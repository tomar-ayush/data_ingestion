import { Database, Import, Download as Export, ArrowRight, ArrowLeft } from "lucide-react";

const Infographic = () => (
  <div className="flex flex-col md:flex-row items-center gap-8 justify-center py-8">
    {/* ClickHouse Icon + Label */}
    <div className="flex flex-col items-center">
      <Database size={44} className="text-[#0FA0CE]" />
      <span className="mt-2 font-semibold text-gray-700">ClickHouse</span>
    </div>
    {/* Arrow Right */}
    <ArrowRight size={32} className="text-primary animate-pulse hidden md:block" />
    {/* Center cycle - both ways */}
    <div className="flex flex-row items-center gap-2 md:flex-col md:gap-0">
      {/* ClickHouse → Flatfile */}
      <Import size={32} className="text-violet-500" />
      <span className="mx-1 md:mx-0 text-gray-500 font-bold text-lg">{'↔'}</span>
      {/* Flatfile → ClickHouse */}
      <Export size={32} className="text-blue-500" />
    </div>
    {/* Arrow Left */}
    <ArrowLeft size={32} className="text-primary animate-pulse hidden md:block" />
    {/* Flatfile Icon + Label */}
    <div className="flex flex-col items-center">
      <Database size={44} className="text-[#9b87f5]" />
      <span className="mt-2 font-semibold text-gray-700">Flatfile</span>
    </div>
  </div>
);

export default Infographic;