import { Heart } from "lucide-react";

const Footer = () => (
  <footer className="w-full py-8 text-center text-gray-400 mt-auto bg-white border-t border-gray-100">
    <span>
      &copy; {new Date().getFullYear()} ClickHouse &lt;→&gt; Flatfile Data Ingestion. Made with ❤️ by <a href="https://ayushtomar.tech/" className="underline hover-scale text-primary">Ayush</a>
    </span>
  </footer>
);

export default Footer;